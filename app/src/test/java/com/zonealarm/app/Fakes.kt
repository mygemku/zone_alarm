package com.zonealarm.app

import com.zonealarm.app.data.geocoding.GeocodingRepository
import com.zonealarm.app.data.geocoding.SearchResult
import com.zonealarm.app.data.local.AlarmZoneDao
import com.zonealarm.app.data.local.AlarmZoneEntity
import com.zonealarm.app.data.datastore.SettingsStore
import com.zonealarm.app.data.location.CurrentLocationProvider
import com.zonealarm.app.domain.model.AlarmMode
import com.zonealarm.app.domain.model.AlarmZone
import com.zonealarm.app.geofence.GeofenceController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

fun testZone(
    id: String,
    name: String = "Zone $id",
    mode: AlarmMode = AlarmMode.ONE_SHOT,
    isEnabled: Boolean = true,
) = AlarmZone(
    id = id,
    name = name,
    latitude = -6.1754,
    longitude = 106.8272,
    radiusMeters = 500f,
    mode = mode,
    isEnabled = isEnabled,
    createdAt = 1000L,
)

/** In-memory fake: no Robolectric, no Android framework. */
class FakeAlarmZoneDao : AlarmZoneDao {
    private val store = MutableStateFlow<Map<String, AlarmZoneEntity>>(emptyMap())

    override fun observeAll(): Flow<List<AlarmZoneEntity>> =
        store.map { it.values.sortedByDescending { e -> e.createdAt } }

    override suspend fun getById(id: String): AlarmZoneEntity? = store.value[id]

    override suspend fun upsert(zone: AlarmZoneEntity) {
        store.value = store.value + (zone.id to zone)
    }

    override suspend fun deleteById(id: String) {
        store.value = store.value - id
    }

    override suspend fun setEnabled(id: String, enabled: Boolean) {
        store.value[id]?.let { store.value = store.value + (id to it.copy(isEnabled = enabled)) }
    }

    override suspend fun getEnabled(): List<AlarmZoneEntity> =
        store.value.values.filter { it.isEnabled }
}

class FakeGeofenceController : GeofenceController {
    val registered = mutableListOf<AlarmZone>()
    val unregistered = mutableListOf<String>()
    var failWith: Exception? = null

    override suspend fun register(zone: AlarmZone) {
        failWith?.let { throw it }
        registered += zone
    }

    override suspend fun unregister(zoneId: String) {
        unregistered += zoneId
    }

    override suspend fun unregisterAll(zoneIds: List<String>) {
        unregistered += zoneIds
    }

    override suspend fun refresh(zones: List<AlarmZone>) {
        registered += zones.filter { it.isEnabled }
    }
}

class FakeLocationProvider(
    var result: Pair<Double, Double>? = 1.0 to 2.0,
    var fail: Exception? = null,
) : CurrentLocationProvider {
    override suspend fun lastLocation(): Pair<Double, Double>? {
        fail?.let { throw it }
        return result
    }
}

class FakeSettingsStore(initial: Float = 500f) : SettingsStore {
    private val state = MutableStateFlow(initial)
    override val defaultRadius: Flow<Float> = state
    override suspend fun setDefaultRadius(meters: Float) {
        state.value = meters
    }
}

class FakeGeocodingRepository(
    var results: List<SearchResult> = emptyList(),
    var fail: Exception? = null,
) : GeocodingRepository {
    override suspend fun search(query: String): Result<List<SearchResult>> {
        fail?.let { return Result.failure(it) }
        return Result.success(results)
    }
}
