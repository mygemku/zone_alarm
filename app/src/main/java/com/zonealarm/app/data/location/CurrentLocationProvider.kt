package com.zonealarm.app.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

/** Testable seam for "current device location" (Play Fused provider underneath). */
interface CurrentLocationProvider {
    /** Returns (lat, lng) or null when unavailable. Throws SecurityException unpermitted. */
    suspend fun lastLocation(): Pair<Double, Double>?
}

class FusedLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : CurrentLocationProvider {
    @SuppressLint("MissingPermission")
    override suspend fun lastLocation(): Pair<Double, Double>? {
        val fused = LocationServices.getFusedLocationProviderClient(context)
        val last = fused.lastLocation.await()
        if (last != null) {
            return last.latitude to last.longitude
        }
        val cts = CancellationTokenSource()
        val current = fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token).await()
        return current?.let { it.latitude to it.longitude }
    }
}
