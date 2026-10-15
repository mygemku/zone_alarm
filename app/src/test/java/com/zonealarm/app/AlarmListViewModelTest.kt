package com.zonealarm.app

import com.zonealarm.app.data.local.toEntity
import com.zonealarm.app.data.repo.ZoneRepositoryImpl
import com.zonealarm.app.ui.list.AlarmListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlarmListViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeAlarmZoneDao
    private lateinit var geo: FakeGeofenceController
    private lateinit var vm: AlarmListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        dao = FakeAlarmZoneDao()
        geo = FakeGeofenceController()
        vm = AlarmListViewModel(ZoneRepositoryImpl(dao), geo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun toggleOnRegistersAndEnables() = runTest(dispatcher) {
        dao.upsert(testZone("1", isEnabled = false).toEntity())
        advanceUntilIdle()
        vm.toggle(testZone("1", isEnabled = false), true)
        advanceUntilIdle()
        assertEquals(listOf(testZone("1", isEnabled = true)), geo.registered)
        assertEquals(true, dao.getById("1")!!.isEnabled)
    }

    @Test
    fun toggleOffUnregistersAndDisables() = runTest(dispatcher) {
        dao.upsert(testZone("1").toEntity())
        advanceUntilIdle()
        vm.toggle(testZone("1"), false)
        advanceUntilIdle()
        assertEquals(listOf("1"), geo.unregistered)
        assertEquals(false, dao.getById("1")!!.isEnabled)
    }

    @Test
    fun registerFailureSurfacesError() = runTest(dispatcher) {
        geo.failWith = SecurityException("denied")
        vm.toggle(testZone("1", isEnabled = false), true)
        advanceUntilIdle()
        assertEquals(
            "Location permission is needed to monitor zones.",
            vm.error.value,
        )
        assertNull(dao.getById("1"))
    }

    @Test
    fun deleteUnregistersAndRemoves() = runTest(dispatcher) {
        dao.upsert(testZone("1").toEntity())
        advanceUntilIdle()
        vm.delete(testZone("1"))
        advanceUntilIdle()
        assertEquals(listOf("1"), geo.unregistered)
        assertNull(dao.getById("1"))
    }
}
