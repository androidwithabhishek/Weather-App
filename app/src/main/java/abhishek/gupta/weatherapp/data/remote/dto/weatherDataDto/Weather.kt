package abhishek.gupta.weatherapp.data.remote.dto.weatherDataDto

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)