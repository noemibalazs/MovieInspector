package com.noemi.movieinspector.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.noemi.movieinspector.R
import com.noemi.movieinspector.model.Movie

@Composable
fun NoNetworkConnection(modifier: Modifier = Modifier) {

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.no_internet),
            contentDescription = null,
            modifier = modifier.size(210.dp)
        )
    }
}

@Composable
fun MovieProgressIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .testTag(stringResource(id = R.string.label_progress_indicator_tag)),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        strokeWidth = 3.dp
    )
}

@Composable
fun MovieGrid(movies: List<Movie>, onMovieClicked: (Movie) -> Unit, modifier: Modifier = Modifier) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        content = {
            items(
                count = movies.size,
                key = { index -> movies[index].id }
            ) { index ->
                MovieItem(movie = movies[index], onMovieClicked = onMovieClicked)
            }
        }
    )
}

@Composable
fun MovieLazyGrid(movies: LazyPagingItems<Movie>, onMovieClicked: (Movie) -> Unit, modifier: Modifier = Modifier) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag(stringResource(id = R.string.label_lazy_column_tag)),
        content = {
            items(
                count = movies.itemCount
            ) { index ->
                movies[index]?.let { movie ->
                    MovieItem(movie = movie, onMovieClicked = onMovieClicked)
                }
            }
        }
    )
}

@Composable
fun MovieItem(movie: Movie, onMovieClicked: (Movie) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .clickable {
                onMovieClicked
                    .invoke(movie)
                    .also { context.toMovieDetails(movie.id) }
            }
            .testTag(stringResource(id = R.string.label_movie_item_tag)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = movie.getPoster(context),
            contentDescription = stringResource(id = R.string.label_movie_avatar),
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun MovieHeader(id: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = id),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = modifier.padding(top = 20.dp)
    )
}