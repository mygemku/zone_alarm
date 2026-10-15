package com.zonealarm.app

import androidx.lifecycle.SavedStateHandle
import com.zonealarm.app.data.local.toEntity
import com.zonealarm.app.data.repo.ZoneRepositoryImpl
import com.zonealarm.app.domain.model.AlarmMode
import com.zonealarm.app.ui.editor.ZoneEditorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ZoneEditorViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeAlarmZoneDao
    private lateinit var geo: FakeGeofenceController
    private lateinit var location: FakeLocationProvider
    private lateinit var settings: FakeSettingsStore
    private lateinit var geocoding: FakeGeocodingRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        dao = FakeAlarmZoneDao()
        geo = FakeGeofenceController()
        location = FakeLocationProvider()
        settings = FakeSettingsStore(initial = 300f)
        geocoding = FakeGeocodingRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun newVm() = ZoneEditorViewModel(
        ZoneRepositoryImpl(dao),
        geo,
        settings,
        location,
        geocoding,
        SavedStateHandle(),
    )

    @Test
    fun blankNameBlocksSave() = runTest(dispatcher) {
        val vm = newVm()
        advanceUntilIdle()
        var done = false
        vm.save { done = true }
        advanceUntilIdle()
        assertEquals("Give the alarm a name.", vm.state.value.error)
        assertFalse(done)
        assertTrue(geo.registered.isEmpty())
    }

    @Test
    fun saveNewRegistersAndFinishes() = runTest(dispatcher) {
        val vm = newVm()
        advanceUntilIdle()
        assertEquals(300f, vm.state.value.radiusMeters)
        vm.onNameChange("Office")
        vm.onModeChange(AlarmMode.RECURRING)
        var done = false
        vm.save { done = true }
        advanceUntilIdle()
        assertTrue(done)
        assertEquals(1, geo.registered.size)
        val saved = geo.registered.single()
        assertEquals("Office", saved.name)
        assertEquals(AlarmMode.RECURRING, saved.mode)
        assertNotNull(dao.getById(saved.id))
    }

    @Test
    fun editExistingLoadsAndUpdates() = runTest(dispatcher) {
        dao.upsert(testZone("9", name = "Old").toEntity())
        val vm = ZoneEditorViewModel(
            ZoneRepositoryImpl(dao),
            geo,
            settings,
            location,
            geocoding,
            SavedStateHandle(mapOf("zoneId" to "9")),
        )
        advanceUntilIdle()
        assertEquals("Old", vm.state.value.name)
        assertFalse(vm.state.value.isNew)
        vm.onNameChange("New")
        var done = false
        vm.save { done = true }
        advanceUntilIdle()
        assertTrue(done)
        assertEquals("New", dao.getById("9")!!.name)
    }

    @Test
    fun currentLocationUnavailableSurfacesError() = runTest(dispatcher) {
        location.result = null
        val vm = newVm()
        advanceUntilIdle()
        vm.useCurrentLocation()
        advanceUntilIdle()
        assertEquals("Current location unavailable.", vm.state.value.error)
    }

    @Test
    fun currentLocationUpdatesPosition() = runTest(dispatcher) {
        location.result = 10.0 to 20.0
        val vm = newVm()
        advanceUntilIdle()
        vm.useCurrentLocation()
        advanceUntilIdle()
        assertEquals(10.0, vm.state.value.latitude, 0.0)
        assertEquals(20.0, vm.state.value.longitude, 0.0)
        assertNull(vm.state.value.error)
    }

    @Test
    fun searchPopulatesResults() = runTest(dispatcher) {
        geocoding.results = listOf(
            com.zonealarm.app.data.geocoding.SearchResult("Jakarta, Indonesia", -6.2, 106.8),
        )
        val vm = newVm()
        advanceUntilIdle()
        vm.onSearchQueryChange("jakarta")
        vm.search()
        advanceUntilIdle()
        assertEquals(1, vm.state.value.searchResults.size)
        assertEquals("Jakarta, Indonesia", vm.state.value.searchResults[0].displayName)
    }

    @Test
    fun searchResultSelectedUpdatesPosition() = runTest(dispatcher) {
        val vm = newVm()
        advanceUntilIdle()
        vm.onSearchResultSelected(com.zonealarm.app.data.geocoding.SearchResult("Test", 1.0, 2.0))
        assertEquals(1.0, vm.state.value.latitude, 0.0)
        assertEquals(2.0, vm.state.value.longitude, 0.0)
        assertTrue(vm.state.value.searchResults.isEmpty())
    }
}
