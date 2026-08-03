package abhishek.gupta.weatherapp.domain.repository

import abhishek.gupta.weatherapp.domain.domainModel.DomainAqiData
import abhishek.gupta.weatherapp.domain.domainModel.DomainCityWeatherResponse
import abhishek.gupta.weatherapp.domain.domainModel.DomainForecastData
import abhishek.gupta.weatherapp.domain.domainModel.DomainSuggestedCity

interface WeatherRepository {



    suspend fun getWeatherDataByCity(city: String, apiKey: String): DomainCityWeatherResponse

    suspend fun getAqi(lat: Double,lon: Double,apiKey: String): DomainAqiData

    suspend fun getCitySuggestedData(cityName: String,apiKey: String): List<DomainSuggestedCity>

    suspend fun getHourlyWeather(city: String, apiKey: String): List<DomainForecastData>


}