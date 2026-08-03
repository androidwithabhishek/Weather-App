package abhishek.gupta.weatherapp.data.remote.dto.weatherDataDto

data class Main(
    val feels_like: Double,
    val grnd_level: Int,
    val humidity: Int,
    val pressure: Int,
    val sea_level: Int,
    val temp: Double,
    val temp_max: Double,
    val temp_min: Double,
    val aqi: Int
)