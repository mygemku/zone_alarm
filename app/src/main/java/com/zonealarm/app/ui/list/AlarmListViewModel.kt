package com.zonealarm.app.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zonealarm.app.domain.model.AlarmZone
import com.zonealarm.app.domain.repo.ZoneRepository
import com.zonealarm.app.geofence.GeofenceController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AlarmListViewModel @Inject constructor(
    private val repository: ZoneRepository,
    private val geofences: GeofenceController,
) : ViewModel() {
    val zones: StateFlow<List<AlarmZone>> = repository.observeZones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    fun toggle(zone: AlarmZone, enabled: Boolean) {
        viewModelScope.launch {
            try {
                if (enabled) geofences.register(zone.copy(isEnabled = true))
                else geofences.unregister(zone.id)
                repository.setEnabled(zone.id, enabled)
            } catch (e: SecurityException) {
                _error.value = "Location permission is needed to monitor zones."
            } catch (e: Exception) {
                _error.value = "Could not update geofence: ${e.message}"
            }
        }
    }

    fun delete(zone: AlarmZone) {
        viewModelScope.launch {
            try {
                geofences.unregister(zone.id)
            } catch (_: Exception) {
                // Zone may never have been registered; still remove it locally.
            }
            repository.removeZone(zone.id)
        }
    }
}
