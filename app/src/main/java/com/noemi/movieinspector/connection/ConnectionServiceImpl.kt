package com.noemi.movieinspector.connection

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class ConnectionServiceImpl @Inject constructor(
    private val manager: ConnectivityManager,
    scope: CoroutineScope
) : ConnectionService {

    private val callBack = NetworkCallBack()

    private val networkState = MutableStateFlow(getDefaultNetworkConnection())

    override val isConnected: StateFlow<Boolean> = networkState.map { it.isConnected() }.stateIn(
        scope = scope,
        initialValue = networkState.value.isConnected(),
        started = SharingStarted.WhileSubscribed()
    )

    override fun startListenNetworkState() {
        if (networkState.value.isListening) return

        networkState.update {
            it.copy(isListening = true)
        }

        manager.registerDefaultNetworkCallback(callBack)
    }

    override fun stopListenNetworkState() {
        if (!networkState.value.isListening) return

        networkState.update {
            it.copy(isListening = false)
        }
        manager.unregisterNetworkCallback(callBack)
    }

    private inner class NetworkCallBack : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            networkState.update {
                it.copy(isAvailable = true)
            }
        }

        override fun onUnavailable() {
            super.onUnavailable()
            networkState.update {
                it.copy(isAvailable = false, networkCapabilities = null)
            }
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            networkState.update {
                it.copy(isAvailable = false)
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            networkState.update {
                it.copy(isAvailable = false, networkCapabilities = null)
            }
        }

        override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
            super.onBlockedStatusChanged(network, blocked)
            networkState.update {
                it.copy(isBlocked = blocked)
            }
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            networkState.update {
                it.copy(networkCapabilities = networkCapabilities)
            }
        }

    }

    private fun getDefaultNetworkConnection() = NetworkConnection(
        isAvailable = false,
        isListening = false,
        isBlocked = true,
        networkCapabilities = null
    )

    private fun NetworkConnection.isConnected(): Boolean =
        isAvailable && isListening && !isBlocked && networkCapabilities?.hasValidCapability() ?: false

    private fun NetworkCapabilities.hasValidCapability(): Boolean = when {
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                (hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) -> true

        else -> false
    }
}