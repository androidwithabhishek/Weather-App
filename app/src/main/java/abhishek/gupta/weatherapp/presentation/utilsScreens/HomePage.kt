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
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.material3.SuggestionChipDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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

/**
 * Modernized weather condition -> (icon, accent color, gradient) mapping.
 * Centralized so the gradient background of the main card and icon tint
 * always stay in sync.
 */
private data class WeatherVisual(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accent: Color,
    val gradient: List<Color>,
)

private fun weatherVisualFor(iconCode: String?): WeatherVisual = when (iconCode) {
    "01d", "01n" -> WeatherVisual(
        Icons.Default.WbSunny,
        Color(0xFFFB8C00),
        listOf(Color(0xFFFFCA5C), Color(0xFFFB8C00))
    )
    "02d", "02n" -> WeatherVisual(
        Icons.Default.CloudQueue,
        Color(0xFF78909C),
        listOf(Color(0xFF90A4AE), Color(0xFF607D8B))
    )

    "03d", "03n", "04d", "04n" -> WeatherVisual(
        Icons.Default.Cloud,
        Color(0xFF78909C),
        listOf(Color(0xFF9E9E9E), Color(0xFF616161))
    )

    "09d", "09n", "10d", "10n" -> WeatherVisual(
        Icons.Default.Grain,
        Color(0xFF42A5F5),
        listOf(Color(0xFF64B5F6), Color(0xFF1E88E5))
    )

    "11d", "11n" -> WeatherVisual(
        Icons.Default.FlashOn,
        Color(0xFFFF7043),
        listOf(Color(0xFFFF8A65), Color(0xFFE64A19))
    )

    "13d", "13n" -> WeatherVisual(
        Icons.Default.AcUnit,
        Color(0xFF26C6DA),
        listOf(Color(0xFF4DD0E1), Color(0xFF00838F))
    )

    "50d", "50n" -> WeatherVisual(
        Icons.Default.FilterDrama,
        Color(0xFF90A4AE),
        listOf(Color(0xFFB0BEC5), Color(0xFF78909C))
    )

    else -> WeatherVisual(
        Icons.Default.HelpOutline,
        Color(0xFF9E9E9E),
        listOf(Color(0xFFBDBDBD), Color(0xFF757575))
    )
}

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

    // Background gradient reacts to the current condition for a livelier feel.
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.background
        )
    )

    // --- Hourly forecast row ---
    val hourlyWeather by homeViewmodel.localForecastData.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .clip(RoundedCornerShape(28.dp))
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
                placeholder = {
                    Text("Search for a city", color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                trailingIcon = {
                    AnimatedVisibility(visible = query.isNotEmpty()) {
                        IconButton(onClick = {
                            query = ""
                            homeViewmodel.updateQuery(query)
                            focusManager.clearFocus()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = androidx.compose.material3.SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            ) {}

            AnimatedVisibility(
                visible = isSuggestionChipsVisible,
                enter = fadeIn(tween(200)) + expandVertically(),
                exit = fadeOut(tween(150)) + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 4.dp),
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
                                label = { Text(weather.cityName) },
                                shape = RoundedCornerShape(50),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                border = null
                            )
                        }
                    }
                }
            }

            val suggestedCities by homeViewmodel.suggestedCities.collectAsStateWithLifecycle()

            AnimatedVisibility(
                visible = isSearchBarActive,
                enter = fadeIn(tween(200)) + expandVertically(),
                exit = fadeOut(tween(150)) + shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    ) {
                        items(suggestedCities) { city ->
                            ListItem(
                                headlineContent = {
                                    Text(city.city, fontWeight = FontWeight.Medium)
                                },
                                supportingContent = {
                                    Text(
                                        "${city.formatted}",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                colors = androidx.compose.material3.ListItemDefaults.colors(
                                    containerColor = Color.Transparent
                                ),
                                modifier = Modifier.clickable {
                                    query = city.city
                                    homeViewmodel.updateQuery(city.city)
                                    homeViewmodel.addLocalCityNames(city.city)
                                    isSearchBarActive = false
                                    focusManager.clearFocus()
                                }
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            localWeatherData?.let { weatherData ->
                val visual = weatherVisualFor(weatherData.icon)

                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 90.dp)
                    ) {
                        // --- Hero weather card ---
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(28.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.linearGradient(
                                                colors = visual.gradient,
                                                start = Offset(0f, 0f),
                                                end = Offset(1000f, 1000f)
                                            )
                                        )
                                        .padding(24.dp)
                                ) {
                                    Column {
                                        if (!isNetworkAvailable) {
                                            NoInternetBanner(
                                                onClick = {
                                                    context.startActivity(
                                                        Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                                    )
                                                }
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 8.dp)
                                        ) {
                                            when {
                                                onSearch -> {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(
                                                            8.dp
                                                        )
                                                    ) {
                                                        CircularProgressIndicator(
                                                            color = Color.White,
                                                            strokeWidth = 2.dp,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        Text(
                                                            text = "Searching…",
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = Color.White
                                                        )
                                                    }
                                                }

                                                weatherData.cityName.isNullOrBlank() -> {
                                                    Text(
                                                        text = if (!error) "No city selected" else "Unknown",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color.White.copy(alpha = 0.9f)
                                                    )
                                                }

                                                else -> {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.LocationOn,
                                                            contentDescription = null,
                                                            tint = Color.White.copy(alpha = 0.9f),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = weatherData.cityName,
                                                            style = MaterialTheme.typography.titleLarge,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Icon(
                                            imageVector = visual.icon,
                                            contentDescription = "Weather Icon",
                                            modifier = Modifier
                                                .size(88.dp)
                                                .align(Alignment.CenterHorizontally),
                                            tint = Color.White
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = weatherData.condition,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White.copy(alpha = 0.9f),
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        )

                                        Text(
                                            text = "${weatherData.temperature}°",
                                            style = MaterialTheme.typography.displayLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 64.sp,
                                            modifier = Modifier.align(Alignment.CenterHorizontally),
                                            color = Color.White
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                        ) {
                                            Text(
                                                text = "Low: ${weatherData.temp_min}°",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = Color.White.copy(alpha = 0.85f)
                                            )
                                            Text(
                                                text = "High: ${weatherData.temp_max}°",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = Color.White.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                }
                            }
                        }


                        // --- Details card ---
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 18.dp)
                                ) {
                                    Text(
                                        text = "Details",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
                                    )
                                    androidx.compose.runtime.CompositionLocalProvider(
                                        androidx.compose.material3.LocalContentColor provides MaterialTheme.colorScheme.onSurface
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            WeatherDetailItem(
                                                icon = Icons.Default.WaterDrop,
                                                label = "Humidity",
                                                value = "${weatherData.humidity}%"
                                            )
                                            WeatherDetailItem(
                                                icon = Icons.Default.Compress,
                                                label = "Pressure",
                                                value = "${weatherData.pressure} hPa"
                                            )
                                            WeatherDetailItem(
                                                icon = Icons.Default.Air,
                                                label = "AQI",
                                                value = aqiData?.aqi.toString()
                                            )
                                        }
                                    }
                                }
                            }
                        }


                        hourlyWeather?.let { forecast ->
                            item {
                                Column {
                                    Text(
                                        text = "Hourly Forecast",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                                    )
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        itemsIndexed(forecast.DayData) { index, hourly ->
                                            Log.d("HomeViewModel", "WeatherData updated: $hourly")
                                            val formatter = DateTimeFormatter.ofPattern("h a")
                                            val time = currentTime.plusHours(index.toLong())
                                            val formattedTime = time.format(formatter)
                                            HourlyForecastItem(hourly, time = formattedTime)
                                        }
                                    }
                                }
                            }

                            // --- View forecast entry point ---
                            item {
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
                                    elevation = CardDefaults.cardElevation(2.dp)
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

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "View Forecast",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Hourly & 7-day weather outlook for ${forecast.cityName}",
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
                    }



                    var isAdding by remember { mutableStateOf(false) }

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
                            .padding(bottom = 62.dp, end = 16.dp    ),
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(
                            6.dp
                        )
                    ) {
                        if (isAdding) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add city")
                        }
                    }







                }
            } ?: run {
                EmptyStateContent(isNetworkAvailable = isNetworkAvailable, context = context)
            }
        }



    }
}

@Composable
private fun NoInternetBanner(onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.Black.copy(alpha = 0.25f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = "No Internet",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Connect to Internet",
                color = Color.White,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EmptyStateContent(isNetworkAvailable: Boolean, context: android.content.Context) {
    val sampleWeatherData = DomainCityWeatherResponse(
        cityName = "Search for a city",
        latitude = 0.000,
        longitude = -0.000,
        temperature = 0.0,
        temp_min = 0.0,
        temp_max = 0.0,
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
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isNetworkAvailable) {
                NoInternetBanner(
                    onClick = { context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = sampleWeatherData.cityName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Use the search bar above to check the weather anywhere",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}