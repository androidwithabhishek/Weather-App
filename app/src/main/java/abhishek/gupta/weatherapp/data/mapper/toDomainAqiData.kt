package abhishek.gupta.weatherapp.data.mapper

import abhishek.gupta.weatherapp.data.remote.dto.airQualityDto.AirPollutionResponse
import abhishek.gupta.weatherapp.domain.domainModel.DomainAqiData


fun AirPollutionResponse.toDomainAqiData(): DomainAqiData {

    val aqiValue = list.firstOrNull()?.main?.aqi ?: 0

    return DomainAqiData(aqi = aqiValue)
}
