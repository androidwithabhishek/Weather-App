package abhishek.gupta.weatherapp.presentation.navigation

import abhishek.gupta.weatherapp.presentation.authScreens.AuthScreen
import abhishek.gupta.weatherapp.presentation.authScreens.AuthViewModel
import abhishek.gupta.weatherapp.presentation.authScreens.LoginScreen
import abhishek.gupta.weatherapp.presentation.authScreens.SignUpScreen
import abhishek.gupta.weatherapp.presentation.homeScreen.ForecastChartUI
import abhishek.gupta.weatherapp.presentation.homeScreen.HomeScreen
import abhishek.gupta.weatherapp.presentation.homeScreen.HomeViewmodel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    homeViewmodel: HomeViewmodel,
) {


    val auth = FirebaseAuth.getInstance()


    var currentUser by remember { mutableStateOf(auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener {
            currentUser = it.currentUser
        }
        auth.addAuthStateListener(listener)
        onDispose { auth.removeAuthStateListener(listener) }
    }

    val startDestination = if (currentUser == null) {
        Routes.AuthScreen
    } else {
        Routes.HomeScreen
    }

    NavHost(navController, startDestination = startDestination) {


        composable<Routes.AuthScreen> {


            AuthScreen(
                navController = navController,
                authViewModel = authViewModel,
            )

        }

        composable<Routes.SingInScreen> {


            SignUpScreen(
                authViewModel = authViewModel,
                navController = navController
            )

        }


        composable<Routes.LogInScreen> {


            LoginScreen(
                authViewModel = authViewModel,
                navController = navController
            )

        }


        composable<Routes.HomeScreen> {

            HomeScreen(

                authViewModel = authViewModel,
                navController = navController,
                homeViewModel = homeViewmodel
            )

        }

        composable<Routes.ForecastScreen> {
            val toRoute = it.toRoute<Routes.ForecastScreen>()

            ForecastChartUI(
                city = toRoute.city,
                homeViewModel = homeViewmodel
            )

        }

    }


}