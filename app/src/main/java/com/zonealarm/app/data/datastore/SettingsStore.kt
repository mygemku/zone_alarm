package com.zonealarm.app.data.datastore

import kotlinx.coroutines.flow.Flow

/** Testable seam for app settings. */
interface SettingsStore {
    val defaultRadius: Flow<Float>
    suspend fun setDefaultRadius(meters: Float)
}
