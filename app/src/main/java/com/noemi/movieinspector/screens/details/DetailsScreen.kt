package com.noemi.movieinspector.screens.details

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.noemi.movieinspector.R
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.model.Review
import com.noemi.movieinspector.model.Trailer
import com.noemi.movieinspector.utils.MovieHeader
import com.noemi.movieinspector.utils.MovieProgressIndicator
import com.noemi.movieinspector.utils.getMovieYoutubePath
import com.noemi.movieinspector.utils.getYoutubeScreenShot
import kotlinx.coroutines.launch
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import com.noemi.movieinspector.utils.getPoster

@Composable
fun DetailsScreen(movieId: Int, modifier: Modifier = Modifier) {

    val viewModel = hiltViewModel<MovieDetailsViewModel>()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val movie by viewModel.outcomeState.collectAsStateWithLifecycle()
    val reviews by viewModel.reviewsState.collectAsStateWithLifecycle()
    val trailers by viewModel.trailersState.collectAsStateWithLifecycle()
    val hasConnection by viewModel.networkState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorState.collectAsStateWithLifecycle()
    val isLoading by viewModel.loadingState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            viewModel.getMovieDetails(movieId)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (isLoading) {
            true -> MovieProgressIndicator()
            else -> MotionContainer(
                movie = movie,
                trailers = trailers,
                reviews = reviews,
                activeNetwork = hasConnection,
                onMovieDetails = viewModel::getMovieDetails
            )
        }

        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun MotionContainer(
    movie: Movie,
    trailers: List<Trailer>,
    reviews: List<Review>,
    activeNetwork: Boolean,
    onMovieDetails: () -> Pair<String, String>
) {
    val context = LocalContext.current

    val motionScene = remember {
        context.resources
            .openRawResource(R.raw.motion_scene)
            .readBytes()
            .decodeToString()
    }

    val minHeight = with(LocalDensity.current) { 90.dp.roundToPx().toFloat() }
    val maxHeight = with(LocalDensity.current) { 270.dp.roundToPx().toFloat() }

    val toolbarHeight = remember { mutableStateOf(maxHeight) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val height = toolbarHeight.value

                if (height + available.y > maxHeight) {
                    toolbarHeight.value = maxHeight
                    return Offset(0f, maxHeight - height)
                }

                if (height + available.y < minHeight) {
                    toolbarHeight.value = minHeight
                    return Offset(0f, minHeight - height)
                }

                toolbarHeight.value += available.y
                return Offset(0f, available.y)
            }
        }
    }

    val progress = 1 - (toolbarHeight.value - minHeight) / (maxHeight - minHeight)

    Column {
        MotionLayout(
            motionScene = MotionScene(content = motionScene),
            progress = progress
        ) {

            AsyncImage(
                model = movie.getPoster(context), contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .layoutId("headerImage"),
                contentScale = ContentScale.Crop
            )
            Text(
                modifier = Modifier.layoutId("title"),
                text = movie.title,
                fontSize = 30.sp,
                color = Color.White
            )
        }

        MovieContent(
            movie = movie,
            trailers = trailers,
            reviews = reviews,
            activeNetwork = activeNetwork,
            onMovieDetails = onMovieDetails,
            scrollConnection = nestedScrollConnection
        )
    }
}

@Composable
fun MovieContent(
    movie: Movie,
    trailers: List<Trailer>,
    reviews: List<Review>,
    activeNetwork: Boolean,
    onMovieDetails: () -> Pair<String, String>,
    scrollConnection: NestedScrollConnection,
    modifier: Modifier = Modifier
) {

    val lazyState = rememberLazyListState()
    val size = trailers.size + reviews.size + 3

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .nestedScroll(scrollConnection),
        state = lazyState,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp)
    ) {

        item {
            MovieShortDescription(
                movie = movie,
                state = lazyState,
                size = size
            )
        }

        item {
            MovieHeader(id = R.string.label_trailers)
        }

        items(
            items = trailers,
            key = { trailer -> trailer.key }
        ) { trailer ->
            MovieTrailer(trailer = trailer, hasNetwork = activeNetwork)
        }

        item {
            MovieHeader(id = R.string.label_reviews)
        }

        items(
            items = reviews,
            key = { review -> review.author }
        ) { review ->
            MovieReview(review = review)
        }

        item {
            MovieShareContent(
                state = lazyState,
                onMovieDetails = onMovieDetails
            )
        }
    }
}

@Composable
fun MovieShortDescription(
    movie: Movie,
    state: LazyListState,
    size: Int, modifier: Modifier = Modifier
) {

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(id = R.string.label_summary),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyLarge
            )

            Icon(
                painter = painterResource(id = R.drawable.down),
                contentDescription = null,
                modifier = modifier
                    .size(42.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        scope.launch {
                            state.scrollToItem(size)
                        }
                    }
            )
        }

        Text(
            text = movie.description,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.bodyMedium,
            modifier = modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
        )

        Text(
            text = movie.releaseDate,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
                .align(Alignment.End)
                .padding(top = 20.dp)
        )

        Text(
            text = stringResource(id = R.string.label_rating, movie.rating),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
                .align(Alignment.End)
                .padding(top = 12.dp)
        )
    }
}

@Composable
fun MovieTrailer(trailer: Trailer, hasNetwork: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    println("The trailer is: $trailer")

    Column(modifier = modifier.fillMaxWidth()) {

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(210.dp)
                .padding(top = 12.dp)
                .clickable {

                    when (hasNetwork) {
                        true -> CustomTabsIntent
                            .Builder()
                            .build()
                            .launchUrl(context, Uri.parse(trailer.key.getMovieYoutubePath()))

                        else -> Toast
                            .makeText(context, R.string.label_toast_message, Toast.LENGTH_LONG)
                            .show()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = trailer.key.getYoutubeScreenShot(),
                contentDescription = stringResource(id = R.string.label_movie_trailer),
                placeholder = painterResource(R.drawable.placeholder),
                modifier = modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.placeholder)
            )
        }


        Text(
            text = trailer.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = modifier.padding(top = 12.dp)
        )
    }
}

@Composable
fun MovieReview(review: Review, modifier: Modifier = Modifier) {

    Column(modifier = modifier.fillMaxWidth()) {

        Text(
            text = review.author,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = modifier.padding(top = 12.dp)
        )

        Text(
            text = review.content,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier.padding(top = 6.dp)
        )
    }
}

@Composable
fun MovieShareContent(
    state: LazyListState,
    onMovieDetails: () -> Pair<String, String>, modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    val title = onMovieDetails.invoke().first
    val message = onMovieDetails.invoke().second

    val intent = Intent(Intent.ACTION_SEND)
    intent.apply {
        type = "plain/text"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, message)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 36.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(id = R.drawable.up),
            contentDescription = null,
            modifier = modifier
                .size(42.dp)
                .align(Alignment.CenterVertically)
                .clickable {
                    scope.launch {
                        state.scrollToItem(0)
                    }
                }
        )

        FloatingActionButton(
            modifier = modifier
                .size(64.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            shape = CircleShape,
            onClick = {
                launcher.launch(intent)
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = null
            )
        }
    }
}