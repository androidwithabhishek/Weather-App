package abhishek.gupta.weatherapp.presentation.homeScreen

import abhishek.gupta.weatherapp.data.local.MainDao
import abhishek.gupta.weatherapp.data.local.entity.ForecastEntity
import abhishek.gupta.weatherapp.data.local.entity.PreviousSearchCityDataEntity
import abhishek.gupta.weatherapp.data.local.entity.WeatherDataEntity
import abhishek.gupta.weatherapp.data.mapper.toEntityList
import abhishek.gupta.weatherapp.domain.domainModel.DomainAqiData
import abhishek.gupta.weatherapp.domain.domainModel.DomainCityWeatherResponse
import abhishek.gupta.weatherapp.domain.domainModel.DomainSuggestedCity
import abhishek.gupta.weatherapp.domain.repository.NetworkConnectivityObserver
import abhishek.gupta.weatherapp.domain.repository.WeatherRepository
import abhishek.gupta.weatherapp.utils.AppUtils
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.recursiveFetchMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewmodel @Inject constructor(
    val weatherRepository: WeatherRepository,
    val mainDao: MainDao,
    val networkConnectivityObserver: NetworkConnectivityObserver,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _onSearch = MutableStateFlow(false)
    var onSearch: StateFlow<Boolean> = _onSearch
    private val _onError = MutableStateFlow(false)
    var onError: StateFlow<Boolean> = _onError

    private val _weatherData = MutableStateFlow<DomainCityWeatherResponse?>(null)
    val weatherData = _weatherData.asStateFlow()

    private val _localWeatherData = MutableStateFlow<WeatherDataEntity?>(null)
    val localWeatherData = _localWeatherData.asStateFlow()

    private val _localizedCityName =
        MutableStateFlow<List<PreviousSearchCityDataEntity>>(emptyList())

    val localizedCityNames = _localizedCityName.asStateFlow()

    private val _aqiData = MutableStateFlow<DomainAqiData?>(null)
    val aqiData = _aqiData.asStateFlow()


    private val _networkStatus = networkConnectivityObserver.networkStatus
    val networkStatus = _networkStatus


    private val _localForecastData = MutableStateFlow<ForecastEntity?>(null)
    val localForecastData = _localForecastData.asStateFlow()
    fun updateQuery(q: String) {
        _query.update {
            q
                .trimStart()
                .replace(Regex("\\s+"), " ")
        }
    }

    init {
        getSuggestedCities()
        getWeatherData()
        viewModelScope.launch(Dispatchers.IO) {
            mainDao.getLocalCityNames().collect { weatherList ->
                _localizedCityName.value = weatherList
            }
        }
    }

    @OptIn(FlowPreview::class)
    fun getWeatherData() {
        viewModelScope.launch {
            _query
                .debounce(1000)
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
                .collectLatest { query ->

                    _onSearch.value = true
                    _onError.value = false

                    // Show cached data immediately if available
                    val cachedWeather = withContext(Dispatchers.IO) {
                        mainDao.getWeatherDataByCity(query).firstOrNull()
                    }

                    cachedWeather?.let {
                        _localWeatherData.value = it
                        getForceCastData(it.cityName)
                    }

                    try {
                        coroutineScope {

                            val weather = async(Dispatchers.IO) {
                                weatherRepository.getWeatherDataByCity(
                                    city = query,
                                    apiKey = AppUtils.APIKEY
                                )
                            }.await()

                            val aqi = async(Dispatchers.IO) {
                                weatherRepository.getAqi(
                                    lat = weather.latitude,
                                    lon = weather.longitude,
                                    apiKey = AppUtils.APIKEY
                                )
                            }

                            val forecast = async(Dispatchers.IO) {
                                weatherRepository.getHourlyWeather(
                                    weather.cityName,
                                    AppUtils.APIKEY
                                )
                            }

                            val aqiData = aqi.await()
                            val forecastData = forecast.await()

                            _weatherData.value = weather
                            _aqiData.value = aqiData

                            withContext(Dispatchers.IO) {


                                if (weather.condition != "Unknown") {

                                    mainDao.upsertWeather(
                                        WeatherDataEntity(
                                            cityName = weather.cityName,
                                            latitude = weather.latitude,
                                            longitude = weather.longitude,
                                            temperature = weather.temperature,
                                            condition = weather.condition,
                                            humidity = weather.humidity,
                                            pressure = weather.pressure,
                                            airQualityIndex = weather.airQualityIndex,
                                            temp_max = weather.temp_max,
                                            temp_min = weather.temp_min,
                                            icon = weather.icon,
                                            savedTime = 0L,
                                            time = weather.time
                                        )
                                    )
                                    Log.d("gg", weather.cityName)
                                    Log.d("gg", weather.time)
                                    mainDao.insertForecast(
                                        ForecastEntity(
                                            cityName = weather.cityName,
                                            DayData = forecastData.toEntityList()
                                        )
                                    )

//                                    addLocalCityNames(weather.cityName)
                                }


                            }

                            _localWeatherData.value =
                                withContext(Dispatchers.IO) {
                                    mainDao.getWeatherDataByCity(weather.cityName).firstOrNull()
                                }

                            getForceCastData(weather.cityName)
                        }

                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {

                        e.printStackTrace()
                        if (cachedWeather == null) {
                            _onError.value = true
                        }

                    } finally {
                        _onSearch.value = false
                    }
                }
        }
    }

    fun getForceCastData(q: String) {
        viewModelScope.launch {
            mainDao.getForecast(q).collect {

                _localForecastData.value = it

            }
        }

    }


    private val _suggestedCities = MutableStateFlow(emptyList<DomainSuggestedCity>())
    val suggestedCities = _suggestedCities.asStateFlow()

    @OptIn(FlowPreview::class)
    fun getSuggestedCities() {
        viewModelScope.launch {
            _query.debounce(500.milliseconds)
                .filter {
                    it.isNotBlank()
                }.distinctUntilChanged().collectLatest { query ->
                    flow {
                        val response = weatherRepository.getCitySuggestedData(
                            cityName = query,
                            apiKey = AppUtils.GEOPIFYAPIKEY
                        )
                        emit(response)
                    }.catch {
                        _suggestedCities.value = emptyList()
                    }.collectLatest { response ->
                        _suggestedCities.value = response
                            .filter { it.city.isNotBlank() && it.formatted.isNotBlank() }
                            .take(10)
                    }
                }
        }

    }


    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()


    fun addLocalCityNames(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mainDao.addLocalCityNames(PreviousSearchCityDataEntity(cityName = cityName))
        }
    }


    private val _isLikedData = MutableStateFlow(false)
    var isLikedData: StateFlow<Boolean> = _isLikedData

    private val _lickedCity = MutableStateFlow<List<WeatherDataEntity>>(emptyList())
    val lickedCity: StateFlow<List<WeatherDataEntity>> = _lickedCity

    fun getLickedCity() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {

            _isLikedData.value = true

            val snapshot = firestore.collection("user").document(uid).get().await()

            val lickedCity = snapshot.get("lickedCity") as? List<String> ?: emptyList()

            if (lickedCity.isNotEmpty()) {

                lickedCity.forEach { city ->

                    val updatedCityData = weatherRepository.getWeatherDataByCity(
                        city = city, apiKey = AppUtils.APIKEY
                    )



                    mainDao.getLickedCity(lickedCity).collect {
                        _lickedCity.value = it
                    }


                    mainDao.upsertWeather(
                        WeatherDataEntity(
                            cityName = updatedCityData.cityName,
                            latitude = updatedCityData.latitude,
                            longitude = updatedCityData.longitude,
                            temperature = updatedCityData.temperature,
                            condition = updatedCityData.condition,
                            humidity = updatedCityData.humidity,
                            pressure = updatedCityData.pressure,
                            airQualityIndex = updatedCityData.airQualityIndex,
                            temp_max = updatedCityData.temp_max,
                            temp_min = updatedCityData.temp_min,
                            icon = updatedCityData.icon
                        )
                    )

                    _isLikedData.value = false

                }


            } else {
                _isLikedData.value = false
                _lickedCity.value = emptyList()
            }

        }
    }



    fun addCity(city: String, onResult: (Boolean, String?) -> Unit) {


        val uid = auth.currentUser?.uid ?: return onResult(false, "User not logged in")

        val userRef = firestore.collection("user").document(uid)

        userRef.get().addOnSuccessListener { doc ->
            val lickedCity = doc.get("lickedCity") as? List<String> ?: emptyList()

            if (lickedCity.contains(city)) {

                onResult(false, "Already Cached This City")
            } else {

                userRef.update("lickedCity", FieldValue.arrayUnion(city)).addOnSuccessListener {
                    onResult(true, "Saved successfully")
                }.addOnFailureListener { e ->
                    onResult(false, e.message)
                }
            }
        }.addOnFailureListener { e ->
            onResult(false, e.message)
        }
    }


    fun removeCity(city: String, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "User not logged in")
        val userRef = firestore.collection("user").document(uid)

        userRef.get().addOnSuccessListener { doc ->
            val likedCities = doc.get("lickedCity") as? List<String> ?: emptyList()

            if (!likedCities.contains(city)) {
                onResult(false, "City not found in your list")
            } else {
                userRef.update("lickedCity", FieldValue.arrayRemove(city)).addOnSuccessListener {
                    onResult(true, "City removed successfully")
                }.addOnFailureListener { e ->
                    onResult(false, e.message)
                }
            }
        }.addOnFailureListener { e ->
            onResult(false, e.message)
        }
    }


    fun clearAppDataSafely(
        onDelete: (success: Boolean, message: String) -> Unit,
    ) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            val userRef = firestore.collection("user").document(uid)

            try {

                userRef.update("lickedCity", emptyList<String>()).await()
                mainDao.clearAllData()
                onDelete(true, "All data removed successfully")


            } catch (e: Exception) {
                e.printStackTrace()
                onDelete(false, "Failed to clear cache : ${e.message}")
            }
        }
    }

}