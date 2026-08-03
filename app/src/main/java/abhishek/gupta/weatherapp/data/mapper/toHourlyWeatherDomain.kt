package abhishek.gupta.weatherapp.data.mapper

import abhishek.gupta.weatherapp.data.remote.dto.hourlyWeatherData.HourlyWeatherData
import abhishek.gupta.weatherapp.domain.domainModel.DomainForecastData

fun HourlyWeatherData.toHourlyWeatherDomain(): List<DomainForecastData> {
    return this.hourlyList.map { item ->
        val weather = item.weatherInfo.firstOrNull()
        DomainForecastData(
            time = item.forecastTime,
            temperature = item.temperatureInfo.temperature,
            condition = weather?.condition ?: "Unknown",
            description = weather?.description ?: "Unknown",
            icon = weather?.iconCode ?: ""
        )
    }
}
