package com.noemi.movieinspector.screens.favorite

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.noemi.movieinspector.utils.MovieGrid
import com.noemi.movieinspector.utils.MovieProgressIndicator

@Composable
fun FavoriteScreen() {

    val viewModel = hiltViewModel<FavoriteViewModel>()
    val context = LocalContext.current

    val movies by viewModel.outcomeState.collectAsStateWithLifecycle()
    val isLoading by viewModel.loadingState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (isLoading) {
            true -> MovieProgressIndicator()
            else -> MovieGrid(movies = movies, onMovieClicked = viewModel::saveMovie)
        }

        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

}