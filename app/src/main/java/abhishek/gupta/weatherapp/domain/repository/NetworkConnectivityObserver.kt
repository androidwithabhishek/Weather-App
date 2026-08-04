package abhishek.gupta.weatherapp.domain.repository


import kotlinx.coroutines.flow.StateFlow


interface NetworkConnectivityObserver {


    val networkStatus: StateFlow<NetworkStatus>


}

sealed class NetworkStatus {
    data object Connected: NetworkStatus()
    data object Disconnected: NetworkStatus()
}