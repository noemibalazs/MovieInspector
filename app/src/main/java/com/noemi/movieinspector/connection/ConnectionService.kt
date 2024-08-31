package com.noemi.movieinspector.connection

import kotlinx.coroutines.flow.StateFlow

interface ConnectionService {

    val isConnected: StateFlow<Boolean>

    fun startListenNetworkState()

    fun stopListenNetworkState()
}