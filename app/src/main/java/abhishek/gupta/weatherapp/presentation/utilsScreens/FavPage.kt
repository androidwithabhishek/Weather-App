package abhishek.gupta.weatherapp.presentation.utilsScreens

import abhishek.gupta.weatherapp.R
import abhishek.gupta.weatherapp.data.local.entity.WeatherDataEntity
import abhishek.gupta.weatherapp.presentation.authScreens.AuthViewModel
import abhishek.gupta.weatherapp.presentation.homeScreen.HomeViewmodel
import abhishek.gupta.weatherapp.presentation.navigation.Routes
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavPage(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewmodel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val likedCities by homeViewModel.lickedCity.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()

    var isLoading = homeViewModel.isLikedData.collectAsState().value


    LaunchedEffect(Unit) {

        homeViewModel.getLickedCity()
    }

    val context = LocalContext.current


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cached Weather Data",modifier.padding(start = 6.dp),
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorResource(id = R.color.app))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (likedCities.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            text = "No cached cities data yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    state = listState
                ) {
                    items(likedCities) { weatherEntity ->
                        WeatherCard(
                            weatherEntity = weatherEntity,

                            onDelete = { city ->

                                homeViewModel.removeCity(city, onResult = { condition, message ->

                                    if (condition) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        homeViewModel.getLickedCity()
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }

                                })

                            },

                            onClick = {
                                homeViewModel.updateQuery(it)
                                navController.navigate(Routes.HomeScreen)
                            },
                        )
                    }
                    item(1) {
                        Spacer(modifier = Modifier.height(32.dp))

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherCard(
    weatherEntity: WeatherDataEntity,
    modifier: Modifier = Modifier,
    onDelete: (city: String) -> Unit,
    onClick: (city: String) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth().clickable{

                onClick(weatherEntity.cityName)
            }
            .clip(RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            val (icon, tint) = when (weatherEntity.icon) {
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
            Icon(
                imageVector = icon,
                contentDescription = weatherEntity.condition,
                modifier = Modifier.size(48.dp),
                tint = tint
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = weatherEntity.cityName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${weatherEntity.temperature}°C",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = weatherEntity.condition,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            Column(
                modifier = Modifier
                    .sizeIn(
                        minWidth = 48.dp,
                        minHeight = 48.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        onDelete(weatherEntity.cityName)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove city",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
//                Text(
//                    text = "Remove",
//                    style = MaterialTheme.typography.labelSmall,
//                    color = MaterialTheme.colorScheme.primary
//                )
            }

        }
    }
}