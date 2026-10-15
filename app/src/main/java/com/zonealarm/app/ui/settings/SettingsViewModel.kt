package com.zonealarm.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zonealarm.app.data.datastore.SettingsRepository
import com.zonealarm.app.data.datastore.SettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingsStore,
) : ViewModel() {
    val defaultRadius: StateFlow<Float> = settings.defaultRadius
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SettingsRepository.DEFAULT_RADIUS_METERS,
        )

    fun setDefaultRadius(meters: Float) {
        viewModelScope.launch { settings.setDefaultRadius(meters) }
    }
}
