package abhishek.gupta.weatherapp.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Routes {


    @Serializable
    data object AuthScreen

    @Serializable
    data object SingInScreen

    @Serializable
    data object LogInScreen

    @Serializable
    data object HomeScreen
    @Serializable
    data class ForecastScreen(val city: String)

}