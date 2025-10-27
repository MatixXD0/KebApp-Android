package pl.kul.kebapp.map

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object GeocodingService {
    private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

    val api: GeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingApi::class.java)
    }
}
