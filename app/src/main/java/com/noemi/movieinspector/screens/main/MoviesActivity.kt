package com.noemi.movieinspector.screens.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.noemi.movieinspector.connection.ConnectionService
import com.noemi.movieinspector.ui.theme.MovieInspectorTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MoviesActivity : ComponentActivity() {

    private val viewModel: MoviesViewModel by viewModels()

    @Inject
    lateinit var service: ConnectionService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                service.isConnected.onEach {
                    viewModel.onNetworkStateChanged(it)
                }.launchIn(lifecycleScope)
            }
        }

        setContent {
            MovieInspectorTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
                    MoviesApp()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MovieInspectorTheme {}
}