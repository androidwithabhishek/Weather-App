package abhishek.gupta.weatherapp.data.local

import abhishek.gupta.weatherapp.data.local.entity.ForecastEntity
import abhishek.gupta.weatherapp.data.local.entity.PreviousSearchCityDataEntity
import abhishek.gupta.weatherapp.data.local.entity.WeatherDataEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MainDao {


    @Upsert
    suspend fun upsertWeather(weatherDataEntity: WeatherDataEntity)

    @Query("""
SELECT *
FROM weather_table
WHERE LOWER(TRIM(cityName)) = LOWER(TRIM(:cityName))
LIMIT 1
""")
    fun getWeatherDataByCity(cityName: String): Flow<WeatherDataEntity?>

    @Query("DELETE FROM weather_table")
    suspend fun clearWeatherDataEntity()

//Forecast

    @Query(
        """
    SELECT * FROM forecast_table
    WHERE REPLACE(LOWER(cityName), ' ', '') LIKE '%' || REPLACE(LOWER(:city), ' ', '') || '%'
    LIMIT 1
"""
    )
    fun getForecast(city: String): Flow<ForecastEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: ForecastEntity)







    @Query("SELECT * FROM city_history ORDER BY cityName ASC")
    fun getLocalCityNames(): Flow<List<PreviousSearchCityDataEntity>>


    @Upsert
    suspend fun addLocalCityNames(previousSearchCityDataEntity: PreviousSearchCityDataEntity)




    @Query("SELECT * FROM weather_table WHERE cityName IN (:citys)")
    fun getLickedCity(citys: List<String>): Flow<List<WeatherDataEntity>>


    @Query("DELETE FROM weather_table")
    suspend fun clearWeatherData()

    @Query("DELETE FROM forecast_table")
    suspend fun clearForecastData()

    @Query("DELETE FROM city_history")
    suspend fun clearSuggestedData()




    @Transaction
    suspend fun clearAllData(){

        clearWeatherData()
        clearForecastData()
        clearSuggestedData()

    }





}