package com.zonealarm.app.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmZoneDao {
    @Query("SELECT * FROM alarm_zones ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<AlarmZoneEntity>>

    @Query("SELECT * FROM alarm_zones WHERE id = :id")
    suspend fun getById(id: String): AlarmZoneEntity?

    @Upsert
    suspend fun upsert(zone: AlarmZoneEntity)

    @Query("DELETE FROM alarm_zones WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE alarm_zones SET isEnabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: String, enabled: Boolean)

    @Query("SELECT * FROM alarm_zones WHERE isEnabled = 1")
    suspend fun getEnabled(): List<AlarmZoneEntity>
}
