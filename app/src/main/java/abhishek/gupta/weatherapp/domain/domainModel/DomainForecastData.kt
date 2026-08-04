package abhishek.gupta.weatherapp.domain.domainModel

data class DomainForecastData(
    val time: String,
    val temperature: Double,
    val condition: String,
    val description: String,
    val icon: String
)
