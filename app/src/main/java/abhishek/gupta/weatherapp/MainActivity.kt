package abhishek.gupta.weatherapp

import abhishek.gupta.weatherapp.data.remote.api.WeatherApiService
import abhishek.gupta.weatherapp.domain.repository.NetworkConnectivityObserver
import abhishek.gupta.weatherapp.domain.repository.NetworkStatus
import abhishek.gupta.weatherapp.presentation.authScreens.AuthViewModel
import abhishek.gupta.weatherapp.presentation.homeScreen.HomeViewmodel
import abhishek.gupta.weatherapp.presentation.navigation.Navigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import abhishek.gupta.weatherapp.presentation.theme.WeatherAppTheme
import abhishek.gupta.weatherapp.utils.NetworkStatusBar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @Inject
    lateinit var weatherApiService: WeatherApiService

    @Inject
    lateinit var connectivityObserver: NetworkConnectivityObserver

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
val homeViewmodel: HomeViewmodel = hiltViewModel()
                val nvaController = rememberNavController()
                val status by homeViewmodel.networkStatus.collectAsState()
                var bgColors by remember { mutableStateOf(Color.Red) }
                var message by rememberSaveable { mutableStateOf("") }
                var showStatusBar by remember { mutableStateOf(false) }

                LaunchedEffect(key1 = status) {
                    when (status) {
                        NetworkStatus.Connected -> {
                            message = "Connected To Internet"
                            bgColors = Color.Green
                            delay(2000)
                            showStatusBar = false
                        }
                        NetworkStatus.Disconnected -> {

                            showStatusBar = true
                            message = "No Internet Connected !!"
                            bgColors = Color.Red

                        }

                    }
                }
                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                    NetworkStatusBar(
                        showMessageBar = showStatusBar,
                        message = message,
                        backgroundColor = bgColors
                    )
                }) { innerPadding ->
                    Navigation(
                        modifier = Modifier.padding(innerPadding),
                        navController = nvaController,
                        authViewModel = hiltViewModel<AuthViewModel>(),
                        homeViewmodel = hiltViewModel<HomeViewmodel>()
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherAppTheme {
        Greeting("Android")
    }
}