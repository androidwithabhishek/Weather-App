package abhishek.gupta.weatherapp.domain.domainModel

data class DomainCityWeatherResponse(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val pressure: Int,
    val airQualityIndex: Int,
    val temp_max: Double,
    val temp_min: Double,
    val icon: String,
    val time: String,
    val savedTime: Long
)