package com.noemi.movieinspector.app

import android.app.Application
import com.noemi.movieinspector.connection.ConnectionService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var service: ConnectionService

    override fun onCreate() {
        super.onCreate()

        service.startListenNetworkState()
    }

    override fun onTerminate() {
        super.onTerminate()
        service.stopListenNetworkState()
    }
}