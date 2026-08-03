package abhishek.gupta.weatherapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "city_history")
data class PreviousSearchCityDataEntity(
    @PrimaryKey
    val cityName: String,
)


