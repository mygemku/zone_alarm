package com.zonealarm.app.data.repo

import com.zonealarm.app.data.local.AlarmZoneDao
import com.zonealarm.app.data.local.toDomain
import com.zonealarm.app.data.local.toEntity
import com.zonealarm.app.domain.model.AlarmZone
import com.zonealarm.app.domain.repo.ZoneRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ZoneRepositoryImpl @Inject constructor(
    private val dao: AlarmZoneDao,
) : ZoneRepository {
    override fun observeZones(): Flow<List<AlarmZone>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getZone(id: String): AlarmZone? =
        dao.getById(id)?.toDomain()

    override suspend fun addZone(zone: AlarmZone) {
        dao.upsert(zone.toEntity())
    }

    override suspend fun updateZone(zone: AlarmZone) {
        dao.upsert(zone.toEntity())
    }

    override suspend fun removeZone(id: String) {
        dao.deleteById(id)
    }

    override suspend fun setEnabled(id: String, enabled: Boolean) {
        dao.setEnabled(id, enabled)
    }
}
