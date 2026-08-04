package abhishek.gupta.weatherapp.data.remote.api

import abhishek.gupta.weatherapp.data.remote.dto.weatherDataDto.CityWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {


//    https://api.openweathermap.org/data/2.5/weather?q=Delhi&appid=2918d47481d7d0abd2195b35a3f64a1c&units=metric

    @GET("weather")
    suspend fun getWeatherByCityName(
        @Query("q") city: String,
        @Query("appid") id: String,
        @Query("units") units: String = "metric",
    ): Response<CityWeatherResponse>


}