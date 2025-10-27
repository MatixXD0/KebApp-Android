package pl.kul.kebapp.map

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("geocode/json")
    suspend fun geocode(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ): GeocodingResponse
}
