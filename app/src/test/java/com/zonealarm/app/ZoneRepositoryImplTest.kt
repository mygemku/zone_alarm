package com.zonealarm.app

import app.cash.turbine.test
import com.zonealarm.app.data.repo.ZoneRepositoryImpl
import com.zonealarm.app.domain.model.AlarmMode
import com.zonealarm.app.domain.model.AlarmZone
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ZoneRepositoryImplTest {
    private lateinit var dao: FakeAlarmZoneDao
    private lateinit var repo: ZoneRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeAlarmZoneDao()
        repo = ZoneRepositoryImpl(dao)
    }

    @Test
    fun addAndGetRoundtrip() = runTest {
        val zone = testZone("1", mode = AlarmMode.RECURRING)
        repo.addZone(zone)
        assertEquals(zone, repo.getZone("1"))
    }

    @Test
    fun missingZoneReturnsNull() = runTest {
        assertNull(repo.getZone("nope"))
    }

    @Test
    fun observeEmitsUpdates() = runTest {
        repo.observeZones().test {
            assertEquals(emptyList<AlarmZone>(), awaitItem())
            repo.addZone(testZone("1"))
            assertEquals(listOf(testZone("1")), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateRemoveAndDisable() = runTest {
        repo.addZone(testZone("1"))
        repo.updateZone(testZone("1", name = "Renamed"))
        assertEquals("Renamed", repo.getZone("1")!!.name)
        repo.setEnabled("1", false)
        assertEquals(false, repo.getZone("1")!!.isEnabled)
        repo.removeZone("1")
        assertNull(repo.getZone("1"))
    }
}
