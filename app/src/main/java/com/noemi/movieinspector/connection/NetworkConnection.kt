package com.noemi.movieinspector.connection

import android.net.NetworkCapabilities

data class NetworkConnection(
    val isAvailable: Boolean,
    val isBlocked: Boolean,
    val isListening: Boolean,
    val networkCapabilities: NetworkCapabilities?
)
