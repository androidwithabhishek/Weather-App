package abhishek.gupta.weatherapp.data.remote.dto.hourlyWeatherData

import com.google.gson.annotations.SerializedName

data class HourlyWeatherData(
    @SerializedName("city") val cityInfo: HourlyCity,
    @SerializedName("list") val hourlyList: List<HourlyForecast>
)

data class HourlyCity(
    @SerializedName("name") val cityName: String,
    @SerializedName("coord") val cityCoord: HourlyCoord
)

data class HourlyCoord(
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lon") val longitude: Double
)

data class HourlyForecast(
    @SerializedName("dt_txt") val forecastTime: String,
    @SerializedName("main") val temperatureInfo: HourlyMain,
    @SerializedName("weather") val weatherInfo: List<HourlyCondition>
)

data class HourlyMain(
    @SerializedName("temp") val temperature: Double
)

data class HourlyCondition(
    @SerializedName("main") val condition: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val iconCode: String
)
