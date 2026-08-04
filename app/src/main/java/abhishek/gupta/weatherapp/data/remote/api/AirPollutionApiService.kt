package abhishek.gupta.weatherapp.data.remote.api

import abhishek.gupta.weatherapp.data.remote.dto.airQualityDto.AirPollutionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AirPollutionApiService {
    @GET("air_pollution")
    suspend fun getAirQuality(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",

    ): AirPollutionResponse
}