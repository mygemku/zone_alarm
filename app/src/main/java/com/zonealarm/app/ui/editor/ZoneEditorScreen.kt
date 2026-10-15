package com.zonealarm.app.ui.editor

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.zonealarm.app.ui.components.ModeSelectorRow
import com.zonealarm.app.ui.components.RadiusSliderRow
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

/**
 * Map picker + details editor using MapLibre Native + OpenFreeMap (keyless).
 * Tap the map to place the zone; radius circle + marker follow the selection.
 * Search uses Nominatim (no API key).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneEditorScreen(
    onDone: () -> Unit,
    viewModel: ZoneEditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showSnackbar(it); viewModel.clearError() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isNew) "New Alarm" else "Edit Alarm",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.useCurrentLocation() }) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Locate",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search bar
            val keyboardController = LocalSoftwareKeyboardController.current
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = { Text("Search city, street, or landmark…") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            viewModel.search()
                        }
                    ),
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        keyboardController?.hide()
                        viewModel.search()
                    },
                    enabled = !state.searching,
                ) {
                    if (state.searching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
            if (state.searchResults.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .heightIn(max = 160.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    LazyColumn {
                        items(state.searchResults) { r ->
                            Text(
                                r.displayName,
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.onSearchResultSelected(r) }.padding(12.dp),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // MapLibre map (lifecycle-aware)
            val primaryColor = MaterialTheme.colorScheme.primary
            val isDark = isSystemInDarkTheme()
            val mapStyleUri = if (isDark) "https://tiles.openfreemap.org/styles/dark" else "https://tiles.openfreemap.org/styles/liberty"
            val lifecycle = LocalLifecycleOwner.current.lifecycle
            val mapView = remember {
                MapLibre.getInstance(context)
                MapView(context)
            }
            // Hold a reference to the map object once ready
            val mapRef = remember { mutableStateOf<MapLibreMap?>(null) }
            val styleRef = remember { mutableStateOf<Style?>(null) }

            DisposableEffect(lifecycle, mapView) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_START -> mapView.onStart()
                        Lifecycle.Event.ON_RESUME -> mapView.onResume()
                        Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                        Lifecycle.Event.ON_STOP -> mapView.onStop()
                        Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                        else -> {}
                    }
                }
                lifecycle.addObserver(observer)
                mapView.onCreate(null)
                onDispose {
                    lifecycle.removeObserver(observer)
                    mapView.onDestroy()
                }
            }

            // Setup map instance & click listener once
            LaunchedEffect(mapView) {
                mapView.getMapAsync { map ->
                    mapRef.value = map
                    map.addOnMapClickListener { point ->
                        viewModel.onPositionChange(point.latitude, point.longitude)
                        viewModel.clearSearchResults()
                        true
                    }
                }
            }

            // Load style based on active theme (auto-switches on theme change)
            LaunchedEffect(mapRef.value, mapStyleUri) {
                val map = mapRef.value ?: return@LaunchedEffect
                map.setStyle(Style.Builder().fromUri(mapStyleUri)) { style ->
                    styleRef.value = style
                    updateMapOverlays(map, style, state.latitude, state.longitude, state.radiusMeters, primaryColor)
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(state.latitude, state.longitude),
                            14.0
                        )
                    )
                }
            }

            // React to state changes and update map overlays + camera
            val currentMap = mapRef.value
            val currentStyle = styleRef.value
            LaunchedEffect(currentMap, currentStyle, state.latitude, state.longitude, state.radiusMeters) {
                val map = currentMap ?: return@LaunchedEffect
                val style = currentStyle ?: return@LaunchedEffect
                updateMapOverlays(map, style, state.latitude, state.longitude, state.radiusMeters, primaryColor)
                val currentTarget = map.cameraPosition.target
                val latDiff = Math.abs((currentTarget?.latitude ?: 0.0) - state.latitude)
                val lngDiff = Math.abs((currentTarget?.longitude ?: 0.0) - state.longitude)
                if (latDiff > 0.0001 || lngDiff > 0.0001) {
                    val currentZoom = map.cameraPosition.zoom
                    val targetZoom = if (currentZoom < 12.0) 14.0 else currentZoom
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(state.latitude, state.longitude),
                            targetZoom
                        )
                    )
                }
            }

            AndroidView(
                modifier = Modifier.fillMaxWidth().weight(1f),
                factory = { mapView },
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Alarm name") },
                    placeholder = { Text("e.g. Wake up at Central Station") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                RadiusSliderRow(state.radiusMeters, viewModel::onRadiusChange)
                Spacer(Modifier.height(8.dp))
                ModeSelectorRow(state.mode, viewModel::onModeChange)
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = { viewModel.save(onDone) },
                    enabled = !state.saving,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                ) {
                    Text(
                        if (state.saving) "Saving…" else "Save Alarm",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

private fun updateMapOverlays(
    map: org.maplibre.android.maps.MapLibreMap,
    style: Style,
    lat: Double,
    lng: Double,
    radiusMeters: Float,
    primaryColor: androidx.compose.ui.graphics.Color,
) {
    val colorInt = AndroidColor.argb(
        255,
        (primaryColor.red * 255).toInt(),
        (primaryColor.green * 255).toInt(),
        (primaryColor.blue * 255).toInt(),
    )
    val fillColorInt = AndroidColor.argb(
        38, // ~15% alpha
        (primaryColor.red * 255).toInt(),
        (primaryColor.green * 255).toInt(),
        (primaryColor.blue * 255).toInt(),
    )

    val circleJson = circleGeoJson(lat, lng, radiusMeters.toDouble())
    val existingSource = style.getSourceAs<org.maplibre.android.style.sources.GeoJsonSource>("zone-source")
    if (existingSource != null) {
        existingSource.setGeoJson(circleJson)
    } else {
        val source = org.maplibre.android.style.sources.GeoJsonSource("zone-source", circleJson)
        style.addSource(source)

        val fillLayer = org.maplibre.android.style.layers.FillLayer("zone-fill", "zone-source").withProperties(
            org.maplibre.android.style.layers.PropertyFactory.fillColor(fillColorInt),
            org.maplibre.android.style.layers.PropertyFactory.fillOpacity(0.35f),
        )
        style.addLayer(fillLayer)

        val lineLayer = org.maplibre.android.style.layers.LineLayer("zone-line", "zone-source").withProperties(
            org.maplibre.android.style.layers.PropertyFactory.lineColor(colorInt),
            org.maplibre.android.style.layers.PropertyFactory.lineWidth(2f),
        )
        style.addLayer(lineLayer)
    }

    map.markers.forEach { map.removeMarker(it) }
    map.addMarker(
        org.maplibre.android.annotations.MarkerOptions()
            .position(LatLng(lat, lng))
            .title("Alarm"),
    )
}

private fun circleGeoJson(lat: Double, lng: Double, radiusMeters: Double): String {
    val points = 64
    val coords = (0 until points).joinToString(",") { i ->
        val angle = 2 * Math.PI * i / points
        // Approximate: 1 degree lat ~111km, lng scaled by cos(lat)
        val dx = radiusMeters * Math.cos(angle)
        val dy = radiusMeters * Math.sin(angle)
        val dLat = dy / 111_000.0
        val dLng = dx / (111_000.0 * Math.cos(Math.toRadians(lat)))
        "[${lng + dLng},${lat + dLat}]"
    }
    // Close the ring
    val firstAngle = 0.0
    val dx0 = radiusMeters * Math.cos(firstAngle)
    val dy0 = radiusMeters * Math.sin(firstAngle)
    val dLat0 = dy0 / 111_000.0
    val dLng0 = dx0 / (111_000.0 * Math.cos(Math.toRadians(lat)))
    val closed = "$coords,[${lng + dLng0},${lat + dLat0}]"
    return """{"type":"Feature","geometry":{"type":"Polygon","coordinates":[[$closed]]},"properties":{}}"""
}
