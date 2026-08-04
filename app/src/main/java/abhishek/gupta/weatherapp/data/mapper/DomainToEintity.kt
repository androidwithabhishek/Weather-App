package abhishek.gupta.weatherapp.data.mapper


import abhishek.gupta.weatherapp.data.local.entity.EntityForecastData
import abhishek.gupta.weatherapp.domain.domainModel.DomainForecastData
import kotlin.collections.map

fun List<DomainForecastData>.toEntityList(): List<EntityForecastData> {
    return this.map { domain ->
        EntityForecastData(
            time = domain.time,
            temperature = domain.temperature,
            condition = domain.condition,
            description = domain.description,
            icon = domain.icon
        )
    }
}
