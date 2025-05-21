package com.huyhieu.core.network.network_monitor

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    fun onConnectionChanged(): Flow<Boolean>
    fun isConnected(): Boolean
}