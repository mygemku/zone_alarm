package com.zonealarm.app.data.geocoding

import retrofit2.http.GET
import retrofit2.http.Query

data class NominatimResult(
    val place_id: Long = 0,
    val display_name: String = "",
    val lat: String = "0",
    val lon: String = "0",
    val type: String = "",
)

interface NominatimApi {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5,
        @Query("addressdetails") addressdetails: Int = 1,
    ): List<NominatimResult>
}
