package abhishek.gupta.weatherapp.data.remote.dto.airQualityDto

import com.google.gson.annotations.SerializedName

data class AirPollutionResponse(
    @SerializedName("list")
    val list: List<AirQualityData>
)

data class AirQualityData(
    @SerializedName("main")
    val main: AirQualityMain
)

data class AirQualityMain(
    @SerializedName("aqi")
    val aqi: Int
)
