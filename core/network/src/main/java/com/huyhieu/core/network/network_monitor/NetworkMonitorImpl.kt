package com.huyhieu.core.network.network_monitor

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import com.huyhieu.core.common.extenstion.orFalse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

class NetworkMonitorImpl(
    @ApplicationContext
    private val context: Context,
) : NetworkMonitor {
    private val connectivityManager = context.getSystemService<ConnectivityManager>()
    override fun onConnectionChanged(): Flow<Boolean> = callbackFlow {
        if (connectivityManager == null) {
            channel.trySend(false)
            channel.close()
            return@callbackFlow
        }

        val networks = mutableSetOf<Network>()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                networks.add(network)
                channel.trySend(true)
            }

            override fun onLost(network: Network) {
                networks.remove(network)
                channel.trySend(networks.isNotEmpty())
            }

            override fun onUnavailable() {
                channel.trySend(false)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.conflate()

    override fun isConnected(): Boolean {
        connectivityManager ?: return false
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val state = capabilities?.hasInternet().orFalse()
        return state
    }

    private fun NetworkCapabilities?.hasInternet(): Boolean {
        return this != null
                && this.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && this.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}