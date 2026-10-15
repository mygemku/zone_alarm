package com.zonealarm.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Local store for zone alarms. Version 1 — no migration needed yet.
 * Migration strategy: manual `Migration` objects per version bump once the
 * app ships (never destructive once user data exists in the wild).
 */
@Database(entities = [AlarmZoneEntity::class], version = 1, exportSchema = false)
abstract class ZoneAlarmDatabase : RoomDatabase() {
    abstract fun alarmZoneDao(): AlarmZoneDao
}
