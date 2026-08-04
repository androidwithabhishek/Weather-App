package abhishek.gupta.weatherapp.data.local

import abhishek.gupta.weatherapp.data.local.entity.ForecastEntity
import abhishek.gupta.weatherapp.data.local.entity.PreviousSearchCityDataEntity
import abhishek.gupta.weatherapp.data.local.entity.WeatherDataEntity
import abhishek.gupta.weatherapp.data.local.typeConverter.ForecastConverters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters


@Database(entities = [WeatherDataEntity::class, ForecastEntity::class, PreviousSearchCityDataEntity::class], version = 1
)
@TypeConverters(ForecastConverters::class)
abstract class MainDatabase : RoomDatabase() {


    abstract fun mainDao (): MainDao


}