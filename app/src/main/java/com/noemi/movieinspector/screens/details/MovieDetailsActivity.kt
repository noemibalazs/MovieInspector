package com.noemi.movieinspector.screens.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.noemi.movieinspector.connection.ConnectionService
import com.noemi.movieinspector.ui.theme.MovieInspectorTheme
import com.noemi.movieinspector.utils.KEY_MOVIE_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MovieDetailsActivity : ComponentActivity() {

    private val viewModel: MovieDetailsViewModel by viewModels()

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
            enableEdgeToEdge()

            MovieInspectorTheme {
                val id = intent.extras?.getInt(KEY_MOVIE_ID) ?: 0
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    DetailsScreen(movieId = id)
                }
            }
        }
    }
}