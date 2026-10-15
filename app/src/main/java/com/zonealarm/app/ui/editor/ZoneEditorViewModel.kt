package com.zonealarm.app.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zonealarm.app.data.datastore.SettingsRepository
import com.zonealarm.app.data.datastore.SettingsStore
import com.zonealarm.app.data.geocoding.GeocodingRepository
import com.zonealarm.app.data.geocoding.SearchResult
import com.zonealarm.app.data.location.CurrentLocationProvider
import com.zonealarm.app.domain.model.AlarmMode
import com.zonealarm.app.domain.model.AlarmZone
import com.zonealarm.app.domain.repo.ZoneRepository
import com.zonealarm.app.geofence.GeofenceController
import com.zonealarm.app.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class EditorUiState(
    val name: String = "",
    val latitude: Double = -6.1754, // Monas, Jakarta (mockup default)
    val longitude: Double = 106.8272,
    val radiusMeters: Float = SettingsRepository.DEFAULT_RADIUS_METERS,
    val mode: AlarmMode = AlarmMode.ONE_SHOT,
    val isNew: Boolean = true,
    val saving: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val searching: Boolean = false,
)

@HiltViewModel
class ZoneEditorViewModel @Inject constructor(
    private val repository: ZoneRepository,
    private val geofences: GeofenceController,
    private val settings: SettingsStore,
    private val location: CurrentLocationProvider,
    private val geocoding: GeocodingRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val zoneId: String? = savedStateHandle[Routes.ARG_ZONE_ID]

    private val _state = MutableStateFlow(EditorUiState())
    val state: StateFlow<EditorUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val defaults = settings.defaultRadius.first()
            val existing = zoneId?.let { repository.getZone(it) }
            _state.value = if (existing == null) {
                EditorUiState(radiusMeters = defaults)
            } else {
                EditorUiState(
                    name = existing.name,
                    latitude = existing.latitude,
                    longitude = existing.longitude,
                    radiusMeters = existing.radiusMeters,
                    mode = existing.mode,
                    isNew = false,
                )
            }
        }
    }

    fun onNameChange(name: String) = _state.update { it.copy(name = name) }
    fun onPositionChange(lat: Double, lng: Double) =
        _state.update { it.copy(latitude = lat, longitude = lng) }
    fun onRadiusChange(radius: Float) = _state.update { it.copy(radiusMeters = radius) }
    fun onModeChange(mode: AlarmMode) = _state.update { it.copy(mode = mode) }
    fun clearError() = _state.update { it.copy(error = null) }

    fun onSearchQueryChange(q: String) = _state.update { it.copy(searchQuery = q) }

    fun search() {
        val q = _state.value.searchQuery.trim()
        if (q.length < 2) {
            _state.update { it.copy(error = "Type at least 2 characters to search.") }
            return
        }
        _state.update { it.copy(searching = true, error = null) }
        viewModelScope.launch {
            val result = geocoding.search(q)
            result.onSuccess { list ->
                if (list.isEmpty()) {
                    _state.update { it.copy(searching = false, searchResults = emptyList(), error = "No results found.") }
                } else {
                    _state.update { it.copy(searching = false, searchResults = list) }
                }
            }.onFailure { e ->
                _state.update { it.copy(searching = false, error = "Search failed: ${e.message}") }
            }
        }
    }

    fun onSearchResultSelected(result: SearchResult) {
        _state.update {
            it.copy(
                latitude = result.latitude,
                longitude = result.longitude,
                searchResults = emptyList(),
                searchQuery = result.displayName.take(60),
            )
        }
    }

    fun clearSearchResults() = _state.update { it.copy(searchResults = emptyList()) }

    fun useCurrentLocation() {
        viewModelScope.launch {
            try {
                val (lat, lng) = location.lastLocation() ?: run {
                    _state.update { it.copy(error = "Current location unavailable.") }
                    return@launch
                }
                _state.update { it.copy(latitude = lat, longitude = lng) }
            } catch (e: SecurityException) {
                _state.update { it.copy(error = "Location permission is needed for this.") }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Could not read location: ${e.message}") }
            }
        }
    }

    fun save(onDone: () -> Unit) {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(error = "Give the alarm a name.") }
            return
        }
        if (s.saving) return
        _state.update { it.copy(saving = true, error = null) }
        viewModelScope.launch {
            try {
                val zone = AlarmZone(
                    id = zoneId ?: UUID.randomUUID().toString(),
                    name = s.name.trim(),
                    latitude = s.latitude,
                    longitude = s.longitude,
                    radiusMeters = s.radiusMeters,
                    mode = s.mode,
                    isEnabled = true,
                )
                if (s.isNew) repository.addZone(zone) else repository.updateZone(zone)
                geofences.register(zone)
                onDone()
            } catch (e: SecurityException) {
                _state.update { it.copy(saving = false, error = "Location permission is needed to monitor zones.") }
            } catch (e: Exception) {
                _state.update { it.copy(saving = false, error = "Could not save: ${e.message}") }
            }
        }
    }
}
