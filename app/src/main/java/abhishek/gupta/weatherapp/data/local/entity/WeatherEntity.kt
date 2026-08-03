package abhishek.gupta.weatherapp.data.local.entity

import android.app.appsearch.StorageInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class WeatherDataEntity(
    @PrimaryKey
    val cityName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val temperature: Double = 0.0,
    val condition: String = "",
    val humidity: Int = 0,
    val pressure: Int = 0,
    val airQualityIndex: Int = 0,
    val temp_max: Double = 0.0,
    val temp_min: Double = 0.0,
    val icon: String = "",
    val savedTime : Long = 0L,
    val time: String = ""

)