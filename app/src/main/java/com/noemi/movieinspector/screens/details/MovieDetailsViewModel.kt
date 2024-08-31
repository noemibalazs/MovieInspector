package com.noemi.movieinspector.screens.details

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.noemi.movieinspector.R
import com.noemi.movieinspector.base.BaseViewModel
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.model.Review
import com.noemi.movieinspector.model.Trailer
import com.noemi.movieinspector.repository.MovieRepository
import com.noemi.movieinspector.utils.toMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    application: Application,
    private val repository: MovieRepository
) :
    BaseViewModel<Movie>(application, repository) {

    private var _movie = MutableStateFlow(Movie())
    override val outcomeState: StateFlow<Movie> = _movie.asStateFlow()

    private var _trailers = MutableStateFlow(emptyList<Trailer>())
    val trailersState: StateFlow<List<Trailer>> = _trailers.asStateFlow()

    private var _reviews = MutableStateFlow(emptyList<Review>())
    val reviewsState: StateFlow<List<Review>> = _reviews.asStateFlow()

    private var _loadingState = MutableStateFlow(false)
    override val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private var _errorState = MutableStateFlow("")
    override val errorState: StateFlow<String> = _errorState.asStateFlow()

    private var _networkState = MutableStateFlow(false)
    val networkState = _networkState.asStateFlow()

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch {

            if (movieId == 0) _errorState.emit("Invalid movie id, try it again!")
            else {
                _loadingState.emit(true)
                _movie.emit(repository.getMovie(movieId).toMovie())
                loadTrailersAndReviews(movieId)
            }
        }
    }

    private fun loadTrailersAndReviews(movieId: Int) {
        viewModelScope.launch {

            val trailersSource = repository.loadTrailers(movieId)
            val reviewsSource = repository.loadReviews(movieId)

            combine(trailersSource, reviewsSource) { trailers, reviews ->
                trailers to reviews
            }
                .catch {
                    _loadingState.emit(false)
                    _errorState.emit(it.message ?: "Error while loading trailers and reviews.")
                }
                .collectLatest {
                    _trailers.emit(it.first)
                    _reviews.emit(it.second)

                    _loadingState.emit(false)
                }
        }
    }

    fun onNetworkStateChanged(isActive: Boolean) {
        viewModelScope.launch {
            _networkState.emit(isActive)
        }
    }

    fun getMovieDetails(): Pair<String, String> {
        val title = String.format(context.getString(R.string.label_shared_movie_title), _movie.value.title)
        val message = String.format(
            context.getString(R.string.label_shared_movie_message),
            _movie.value.description,
            _movie.value.rating.toString(),
            _movie.value.releaseDate
        )
        return title to message
    }
}