package com.zonealarm.app.data.geocoding

import javax.inject.Inject
import javax.inject.Singleton

data class SearchResult(
    val displayName: String,
    val latitude: Double,
    val longitude: Double,
)

interface GeocodingRepository {
    suspend fun search(query: String): Result<List<SearchResult>>
}

@Singleton
class NominatimGeocodingRepository @Inject constructor(
    private val api: NominatimApi,
) : GeocodingRepository {
    override suspend fun search(query: String): Result<List<SearchResult>> = try {
        val raw = api.search(query.trim())
        val mapped = raw.mapNotNull {
            val lat = it.lat.toDoubleOrNull() ?: return@mapNotNull null
            val lon = it.lon.toDoubleOrNull() ?: return@mapNotNull null
            SearchResult(it.display_name, lat, lon)
        }
        Result.success(mapped)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
