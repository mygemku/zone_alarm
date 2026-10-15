package com.zonealarm.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zonealarm.app.domain.model.AlarmMode
import com.zonealarm.app.domain.model.AlarmZone

@Entity(tableName = "alarm_zones")
data class AlarmZoneEntity(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
    /** Stored as enum name ([AlarmMode.name]). */
    val mode: String,
    val soundUri: String?,
    val isEnabled: Boolean,
    val createdAt: Long,
)

fun AlarmZoneEntity.toDomain(): AlarmZone = AlarmZone(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    radiusMeters = radiusMeters,
    mode = AlarmMode.valueOf(mode),
    soundUri = soundUri,
    isEnabled = isEnabled,
    createdAt = createdAt,
)

fun AlarmZone.toEntity(): AlarmZoneEntity = AlarmZoneEntity(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    radiusMeters = radiusMeters,
    mode = mode.name,
    soundUri = soundUri,
    isEnabled = isEnabled,
    createdAt = createdAt,
)
