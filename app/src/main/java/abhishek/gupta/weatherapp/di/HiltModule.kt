package abhishek.gupta.weatherapp.di

import abhishek.gupta.weatherapp.data.local.MainDao
import abhishek.gupta.weatherapp.data.local.MainDatabase
import abhishek.gupta.weatherapp.data.remote.api.AirPollutionApiService
import abhishek.gupta.weatherapp.data.remote.api.GeoapifyApiService
import abhishek.gupta.weatherapp.data.remote.api.HourlyApiService
import abhishek.gupta.weatherapp.data.remote.api.WeatherApiService
import abhishek.gupta.weatherapp.data.repositoryImpl.NetworkConnectivityObserverImpl
import abhishek.gupta.weatherapp.data.repositoryImpl.WeatherRepositoryImpl
import abhishek.gupta.weatherapp.domain.repository.NetworkConnectivityObserver
import abhishek.gupta.weatherapp.domain.repository.WeatherRepository
import android.app.Application
import android.content.Context
import androidx.compose.ui.layout.HorizontalRuler
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import kotlin.jvm.java


@InstallIn(SingletonComponent::class)
@Module
object HiltModule {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()

    }


    @Provides
    @Singleton
    @Named("GeoapifyRet")
    fun provideGeoapifyRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.geoapify.com/v1/geocode/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideHourlyWeatherApi(retrofit: Retrofit): HourlyApiService =
        retrofit.create(HourlyApiService::class.java)

    @Provides
    @Singleton
    fun provideGeoapifyApi(@Named("GeoapifyRet") retrofit: Retrofit): GeoapifyApiService {
        return retrofit.create(GeoapifyApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesAqiApi(retrofit: Retrofit): AirPollutionApiService {

        return retrofit.create(AirPollutionApiService::class.java)

    }

    @Provides
    @Singleton
    fun providesWeatherRepository(
        weatherApiService: WeatherApiService,
        airPollutionApiService: AirPollutionApiService,
        geoapifyApiService: GeoapifyApiService,
        hourlyApiService: HourlyApiService
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            weatherApiService = weatherApiService,
            airPollutionApiService,
            geoapifyApiService,
            hourlyApiService = hourlyApiService,
        )
    }

    @Provides
    @Singleton
    fun providesDB(context: Application): MainDatabase {
        return Room.databaseBuilder(context, MainDatabase::class.java, "main_app_db").fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provides(mainDatabase: MainDatabase): MainDao {
        return mainDatabase.mainDao()
    }

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(Dispatchers.IO)


    @Provides
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context,
        scope: CoroutineScope,
    ): NetworkConnectivityObserver {
        return NetworkConnectivityObserverImpl(context, scope)
    }


}