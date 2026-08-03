package abhishek.gupta.weatherapp.presentation.utilsScreens

import abhishek.gupta.weatherapp.R
import abhishek.gupta.weatherapp.data.local.entity.EntityForecastData

import abhishek.gupta.weatherapp.domain.domainModel.DomainCityWeatherResponse
import abhishek.gupta.weatherapp.domain.repository.NetworkStatus
import abhishek.gupta.weatherapp.presentation.authScreens.AuthViewModel
import abhishek.gupta.weatherapp.presentation.homeScreen.HomeViewmodel
import abhishek.gupta.weatherapp.presentation.navigation.Routes
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    homeViewmodel: HomeViewmodel,
    authViewModel: AuthViewModel,
    navController: NavController,
) {
    var query by remember { mutableStateOf("") }

    val aqiData by homeViewmodel.aqiData.collectAsState()
    val context = LocalContext.current

    val domainWeather = homeViewmodel.weatherData
    var onSearch = homeViewmodel.onSearch.collectAsStateWithLifecycle().value
    val fucusRequest = remember { FocusRequester() }

    val isNetworkAvailable =
        homeViewmodel.networkStatus.collectAsState().value == NetworkStatus.Connected
    val currentTime = LocalTime.now()


    val localWeatherData by homeViewmodel.localWeatherData.collectAsStateWithLifecycle()
    val localizedCityNames by homeViewmodel.localizedCityNames.collectAsStateWithLifecycle()


    val error by homeViewmodel.onError.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isSuggestionChipsVisible by remember { mutableStateOf(false) }
    val keyBoardController = LocalSoftwareKeyboardController.current
    var isSearchBarActive by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        isSuggestionChipsVisible = it.isFocused
                        isSearchBarActive = it.isFocused
                    },
                query = query,
                onQueryChange = {
                    query = it
                    homeViewmodel.updateQuery(query)
                },
                onSearch = {
                    homeViewmodel.updateQuery(query)
                    keyBoardController?.hide()
                    focusManager.clearFocus()

                },
                active = false,
                onActiveChange = {},
                placeholder = { Text("Search") },
                trailingIcon = {
                    IconButton(onClick = {
                        if (query.isNotEmpty()) {
                            query = ""
                            focusManager.clearFocus()
                        } else {
                            focusManager.clearFocus()
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "close")
                    }

                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "search")
                }) {

            }

            AnimatedVisibility(
                visible = isSuggestionChipsVisible
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(localizedCityNames) { weather ->
                            SuggestionChip(
                                onClick = {
                                    query = weather.cityName
                                    homeViewmodel.updateQuery(query)
                                    keyBoardController?.hide()
                                    focusManager.clearFocus()
                                },
                                label = {
                                    Text(weather.cityName)
                                }
                            )
                        }
                    }
                }
            }


            val suggestedCities by homeViewmodel.suggestedCities.collectAsStateWithLifecycle()

            AnimatedVisibility(
                visible = isSearchBarActive
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .heightIn(max = 300.dp)
                ) {
                    items(suggestedCities) { city ->

                        ListItem(
                            headlineContent = {
                                Text(city.city)
                            },
                            supportingContent = {
                                Text("${city.formatted}")
                            },
                            leadingContent = {
                                Icon(Icons.Default.LocationOn, null)
                            },
                            modifier = Modifier.clickable {
                                query = city.city
                                homeViewmodel.updateQuery(city.city)
                                homeViewmodel.addLocalCityNames(city.city)
                                isSearchBarActive = false
                                focusManager.clearFocus()

                            }
                        )

                        HorizontalDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(23.dp))

            localWeatherData?.let { weatherData ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Column {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp)
                            ) {


                                if (!isNetworkAvailable) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {

                                                val intent =
                                                    Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                                context.startActivity(intent)
                                            }
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WifiOff,
                                            contentDescription = "No Internet",
                                            tint = Color.Red.copy(alpha = 0.8f),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Connect to Internet",
                                            color = Color.Red.copy(alpha = 0.9f),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp, end = 16.dp)
                                ) {
                                    when {
                                        onSearch -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.padding(8.dp)
                                            ) {
                                                CircularProgressIndicator(
                                                    color = Color.Black,
                                                    strokeWidth = 2.dp,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Text(
                                                    text = "Searching...",
                                                    style = MaterialTheme.typography.headlineSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 22.sp,
                                                    color = Color.Black.copy(alpha = 0.8f)
                                                )
                                            }
                                        }

                                        weatherData.cityName.isNullOrBlank() -> {
                                            if (!error) {

                                                Text(
                                                    text = "No city selected",
                                                    style = MaterialTheme.typography.headlineSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 22.sp,
                                                    color = Color.Black.copy(alpha = 0.8f)
                                                )
                                            } else {
                                                Text(
                                                    text = "Unknown",
                                                    style = MaterialTheme.typography.headlineSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 22.sp,
                                                    color = Color.Black.copy(alpha = 0.8f)
                                                )
                                            }


                                        }

                                        else -> {

                                            Text(
                                                text = weatherData.cityName,
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp,
                                                color = Color.Black
                                            )
                                        }
                                    }


                                }
                                val (icon, tint) = when (weatherData.icon) {
                                    "01d", "01n" -> Icons.Default.WbSunny to Color(0xFFFFD700)
                                    "02d", "02n" -> Icons.Default.CloudQueue to Color.Gray
                                    "03d", "03n", "04d", "04n" -> Icons.Default.Cloud to Color.Gray
                                    "09d", "09n", "10d", "10n" -> Icons.Default.Grain to Color(
                                        0xFF2196F3
                                    )

                                    "11d", "11n" -> Icons.Default.FlashOn to Color(0xFFFF5722)
                                    "13d", "13n" -> Icons.Default.AcUnit to Color(0xFF00BCD4)
                                    "50d", "50n" -> Icons.Default.FilterDrama to Color.Gray
                                    else -> Icons.Default.HelpOutline to Color.Black
                                }

                                val conditionTextColor = when (weatherData.icon) {
                                    "01d", "01n" -> Color(0xFFB8860B) // Dark Goldenrod
                                    "02d", "02n" -> Color(0xFF616161) // Dark Gray
                                    "03d", "03n", "04d", "04n" -> Color(0xFF424242) // Darker Gray
                                    "09d", "09n", "10d", "10n" -> Color(0xFF1565C0) // Dark Blue
                                    "11d", "11n" -> Color(0xFFD84315) // Dark Orange
                                    "13d", "13n" -> Color(0xFF00838F) // Dark Cyan
                                    "50d", "50n" -> Color(0xFF616161) // Dark Gray
                                    else -> Color.Black
                                }

                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Weather Icon",
                                    modifier = Modifier
                                        .size(80.dp)
                                        .align(Alignment.CenterHorizontally),
                                    tint = tint
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = weatherData.condition,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                                    color = conditionTextColor,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )

                                Text(
                                    text = "${weatherData.temperature}°C",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 52.sp,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    color = Color.Black.copy(alpha = 0.9f)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text(
                                        text = "Min: ${weatherData.temp_min}°C",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 18.sp
                                        ),
                                        color = Color(0xFF757575)
                                    )
                                    Text(
                                        text = "Max: ${weatherData.temp_max}°C",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 18.sp
                                        ),
                                        color = Color(0xFF757575)
                                    )
                                }





                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))


                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Details",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 5.dp), color = Color.Black
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    WeatherDetailItem(
                                        icon = Icons.Default.WaterDrop,
                                        label = "Humidity",
                                        value = "${weatherData.humidity}%"
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    WeatherDetailItem(
                                        icon = Icons.Default.Compress,
                                        label = "Pressure",
                                        value = "${weatherData.pressure} hPa"
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    WeatherDetailItem(
                                        icon = Icons.Default.Air,
                                        label = "AQI",
                                        value = aqiData?.aqi.toString()
                                    )
                                }
                            }


                        }

                        Spacer(modifier = Modifier.height(1.dp))
                        val hourlyWeather by homeViewmodel.localForecastData.collectAsState()

                        hourlyWeather?.let {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                itemsIndexed(it.DayData) { index, hourly ->

                                    Log.d("HomeViewModel", "WeatherData updated: ${hourly}")

                                    val formatter = DateTimeFormatter.ofPattern("h a")
                                    val time = currentTime.plusHours(index.toLong())
                                    val formattedTime = time.format(formatter)
                                    HourlyForecastItem(hourly, time = formattedTime)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(17.dp))
                        hourlyWeather?.let {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate(Routes.ForecastScreen(weatherData.cityName))
                                    },
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                ),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 18.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Cloud,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .padding(12.dp)
                                                .size(22.dp)
                                        )
                                    }

                                    Spacer(Modifier.width(20.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "View Forecast",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = "Hourly & 7-day weather outlook for ${it.cityName}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                        }

                    }

                    var isAdding by remember {
                        mutableStateOf(false)
                    }

                    FloatingActionButton(
                        onClick = {
                            isAdding = true
                            homeViewmodel.addCity(
                                weatherData.cityName,
                                onResult = { condition, message ->

                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    isAdding = false

                                })
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp, vertical = 100.dp),
                        containerColor = colorResource(id = R.color.app).copy(alpha = 0.85f)
                    ) {
                        if (isAdding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = Color.Black
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color.Black
                            )
                        }
                    }


                }


            } ?: run {
                val sampleWeatherData = DomainCityWeatherResponse(
                    cityName = "Search For City ",
                    latitude = 0.000,
                    longitude = -0.000,
                    temperature = 00.0,
                    temp_min = 00.0,
                    temp_max = 00.0,
                    condition = "Unknown",
                    humidity = 0,
                    pressure = 0,
                    airQualityIndex = 0,
                    icon = "",
                    time = "",
                    savedTime = 0L
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        if (!isNetworkAvailable) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {

                                        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                        context.startActivity(intent)
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WifiOff,
                                    contentDescription = "No Internet",
                                    tint = Color.Red.copy(alpha = 0.8f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Connect to Internet",
                                    color = Color.Red.copy(alpha = 0.9f),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp, end = 16.dp)
                        ) {
                            Text(
                                text = sampleWeatherData.cityName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                        }


                        Icon(
                            imageVector = Icons.Default.HelpOutline, // question mark
                            contentDescription = sampleWeatherData.condition,
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.CenterHorizontally),
                            tint = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Unknown",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                            color = Color(0xFF757575),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Text(
                            text = "${sampleWeatherData.temperature}°C",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 52.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = Color.Black.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "Min: ${sampleWeatherData.temp_min}°C",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp
                                ),
                                color = Color(0xFF757575)
                            )
                            Text(
                                text = "Max: ${sampleWeatherData.temp_max}°C",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 18.sp
                                ),
                                color = Color(0xFF757575)
                            )
                        }


                        Spacer(modifier = Modifier.height(12.dp))


                    }
                }


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Details",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp),

                            )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            WeatherDetailItem(
                                icon = Icons.Default.WaterDrop,
                                label = "Humidity",
                                value = "${sampleWeatherData.humidity}%"
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            WeatherDetailItem(
                                icon = Icons.Default.Compress,
                                label = "Pressure",
                                value = "${sampleWeatherData.pressure} hPa"
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            WeatherDetailItem(
                                icon = Icons.Default.Air,
                                label = "AQI",
                                value = sampleWeatherData.airQualityIndex.toString()
                            )
                        }
                    }
                }
            }
        }


    }

}