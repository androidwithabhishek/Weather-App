package abhishek.gupta.weatherapp.data.local.entity

import abhishek.gupta.weatherapp.domain.domainModel.DomainForecastData
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.InspectableModifier
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "forecast_table")
data class ForecastEntity(
@PrimaryKey
    val cityName: String,
    val DayData : List<EntityForecastData>

)

data class EntityForecastData (
    val time: String,
    val temperature: Double,
    val condition: String,
    val description: String,
    val icon: String
)
