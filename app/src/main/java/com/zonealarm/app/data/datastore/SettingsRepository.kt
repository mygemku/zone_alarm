package com.zonealarm.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsStore by preferencesDataStore(name = "settings")

/** Minimal app settings. Grows in later phases; single value for MVP. */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : SettingsStore {
    companion object {
        const val DEFAULT_RADIUS_METERS = 500f
        private val KEY_DEFAULT_RADIUS = floatPreferencesKey("default_radius")
    }

    override val defaultRadius: Flow<Float> = context.settingsStore.data
        .map { it[KEY_DEFAULT_RADIUS] ?: DEFAULT_RADIUS_METERS }

    override suspend fun setDefaultRadius(meters: Float) {
        context.settingsStore.edit { it[KEY_DEFAULT_RADIUS] = meters }
    }
}
