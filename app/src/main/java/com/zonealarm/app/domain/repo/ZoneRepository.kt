package com.zonealarm.app.domain.repo

import com.zonealarm.app.domain.model.AlarmZone
import kotlinx.coroutines.flow.Flow

/** Single source of truth for saved zone alarms. Implemented by Room (Phase 4). */
interface ZoneRepository {
    fun observeZones(): Flow<List<AlarmZone>>
    suspend fun getZone(id: String): AlarmZone?
    suspend fun addZone(zone: AlarmZone)
    suspend fun updateZone(zone: AlarmZone)
    suspend fun removeZone(id: String)
    suspend fun setEnabled(id: String, enabled: Boolean)
}
