package abhishek.gupta.weatherapp.data.mapper

import abhishek.gupta.weatherapp.data.remote.dto.weatherDataDto.CityWeatherResponse
import abhishek.gupta.weatherapp.domain.domainModel.DomainCityWeatherResponse
import kotlinx.datetime.LocalDateTime
import java.time.format.DateTimeFormatter


fun CityWeatherResponse.toDomainCityWeatherResponse(): DomainCityWeatherResponse {


    return DomainCityWeatherResponse(
        cityName = this.name ?: "Unknown City",
        latitude = this.coord?.lat ?: 0.0,
        longitude = this.coord?.lon ?: 0.0,
        temperature = this.main.temp,
        condition = this.weather.firstOrNull()?.description ?: "Unknown",
        humidity = this.main.humidity,
        pressure = this.main.pressure,
        airQualityIndex = this.main.aqi,
        temp_min = this.main.temp_min,
        temp_max = this.main.temp_max,
        icon = this.weather.firstOrNull()?.icon ?: "",
        time = this.timezone.toString(),
        savedTime = System.currentTimeMillis(),
    )
}