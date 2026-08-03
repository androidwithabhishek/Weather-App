package abhishek.gupta.weatherapp.data.remote.api

import abhishek.gupta.weatherapp.data.remote.dto.hourlyWeatherData.HourlyWeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface HourlyApiService {

    @GET("forecast")
    suspend fun getHourlyForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
    ): HourlyWeatherData
}