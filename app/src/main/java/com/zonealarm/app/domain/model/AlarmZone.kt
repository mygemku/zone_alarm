package com.zonealarm.app.domain.model

/** Firing behavior of a zone alarm, chosen per alarm. */
enum class AlarmMode {
    ONE_SHOT,
    RECURRING,
}

/** A user-defined geographic zone that triggers an alarm on entry. */
data class AlarmZone(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Float,
    val mode: AlarmMode,
    val soundUri: String? = null,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
)
