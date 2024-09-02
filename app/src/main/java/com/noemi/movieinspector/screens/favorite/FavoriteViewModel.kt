package com.noemi.movieinspector.screens.favorite

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.noemi.movieinspector.base.BaseViewModel
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.repository.MovieRepository
import com.noemi.movieinspector.utils.toMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    application: Application,
    private val repository: MovieRepository
) : BaseViewModel<List<Movie>>(application, repository) {

    private var _favoriteMovies = MutableStateFlow(emptyList<Movie>())
    override val outcomeState: StateFlow<List<Movie>> = _favoriteMovies.asStateFlow()

    private var _loadingState = MutableStateFlow(false)
    override val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private var _errorState = MutableStateFlow("")
    override val errorState: StateFlow<String> = _errorState.asStateFlow()

    init {
        loadFavoriteMovies()
    }

    internal fun loadFavoriteMovies() {
        viewModelScope.launch {
            _loadingState.emit(true)

            repository.observeMovies()
                .catch {
                    _errorState.emit(it.message ?: "Error while loading favorite movies.")
                    _loadingState.emit(false)
                }
                .collectLatest {
                    _loadingState.emit(false)
                    val movies = it.map { entity -> entity.toMovie() }
                    _favoriteMovies.emit(movies)
                }
        }
    }
}