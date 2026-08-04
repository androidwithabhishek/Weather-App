package abhishek.gupta.weatherapp.data.repositoryImpl

import abhishek.gupta.weatherapp.data.mapper.toDomainAqiData
import abhishek.gupta.weatherapp.data.mapper.toDomainCityWeatherResponse
import abhishek.gupta.weatherapp.data.mapper.toDomainSuggestedCity
import abhishek.gupta.weatherapp.data.mapper.toHourlyWeatherDomain
import abhishek.gupta.weatherapp.data.remote.api.AirPollutionApiService
import abhishek.gupta.weatherapp.data.remote.api.GeoapifyApiService
import abhishek.gupta.weatherapp.data.remote.api.HourlyApiService
import abhishek.gupta.weatherapp.data.remote.api.WeatherApiService
import abhishek.gupta.weatherapp.data.remote.dto.hourlyWeatherData.HourlyWeatherData
import abhishek.gupta.weatherapp.domain.domainModel.DomainAqiData
import abhishek.gupta.weatherapp.domain.domainModel.DomainCityWeatherResponse
import abhishek.gupta.weatherapp.domain.domainModel.DomainForecastData
import abhishek.gupta.weatherapp.domain.domainModel.DomainSuggestedCity
import abhishek.gupta.weatherapp.domain.repository.WeatherRepository
import android.util.Log

class WeatherRepositoryImpl(
    private val weatherApiService: WeatherApiService,
    private val airPollutionApi: AirPollutionApiService,
    private val geoapifyApiService: GeoapifyApiService,
    private val  hourlyApiService: HourlyApiService
) : WeatherRepository {

    override suspend fun getWeatherDataByCity(
        city: String,
        apiKey: String,
    ): DomainCityWeatherResponse {

        return try {

            val response = weatherApiService.getWeatherByCityName(
                city = city,
                id = apiKey
            )
            response.body()?.toDomainCityWeatherResponse()
                ?: run {
                    DomainCityWeatherResponse(
                        cityName = city,
                        latitude = 0.0,
                        longitude = 0.0,
                        temperature = 0.0,
                        condition = "Unknown",
                        humidity = 0,
                        pressure = 0,
                        airQualityIndex = -1,
                        temp_max = 0.0,
                        temp_min = 0.0,
                        icon = "",
                        time = "",
                        savedTime = 0L
                    )

                }
        } catch (e: Exception) {

            DomainCityWeatherResponse(
                cityName = city,
                latitude = 0.0,
                longitude = 0.0,
                temperature = 0.0,
                condition = "Unknown",
                humidity = 0,
                pressure = 0,
                airQualityIndex = -1,
                temp_max = 0.0,
                temp_min = 0.0,
                icon = "",
                time = "",
                savedTime = 0L
            )
        }
    }

    override suspend fun getAqi(
        lat: Double,
        lon: Double,
        apiKey: String,
    ): DomainAqiData {


        return try {

            airPollutionApi.getAirQuality(
                lat = lat,
                lon = lon,
                apiKey = apiKey
            ).toDomainAqiData()


        } catch (e: Exception) {

            DomainAqiData(0)
        }


    }




    override suspend fun getHourlyWeather(
        city: String,
        apiKey: String,
    ): List<DomainForecastData> {
        return try {

            val dto = hourlyApiService.getHourlyForecast(city, apiKey)

            dto.toHourlyWeatherDomain()
        } catch (e: Exception) {
            e.printStackTrace()

            emptyList()
        }
    }







    override suspend fun getCitySuggestedData(
        cityName: String,
        apiKey: String,
    ): List<DomainSuggestedCity> {

        if (cityName.length < 4) return emptyList()

        return try {
            val response = geoapifyApiService.getCitySuggestion(city = cityName, apiKey = apiKey)
            response.toDomainSuggestedCity()
        } catch (e: Exception) {
            emptyList()
        }
    }

}