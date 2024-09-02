package com.noemi.movieinspector.screens.popular

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.noemi.movieinspector.base.BaseViewModel
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.paging.popular.PopularPagingConfig
import com.noemi.movieinspector.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularViewModel @Inject constructor(
    application: Application,
    repository: MovieRepository,
    private val popularPagingConfig: PopularPagingConfig
) : BaseViewModel<PagingData<Movie>>(application, repository) {

    private var _pagingMovie: MutableStateFlow<PagingData<Movie>> = MutableStateFlow(PagingData.empty())
    override val outcomeState: StateFlow<PagingData<Movie>> = _pagingMovie.asStateFlow()

    private var _loadingState = MutableStateFlow(false)
    override val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    private var _errorState = MutableStateFlow("")
    override val errorState: StateFlow<String> = _errorState.asStateFlow()

    init {
        loadPopularMovies()
    }

    internal fun loadPopularMovies() {
        viewModelScope.launch {
            _loadingState.emit(true)

            popularPagingConfig.loadMovies()
                .catch {
                    _loadingState.emit(false)
                    _errorState.emit(it.message ?: "Error while loading most popular movies.")
                }
                .cachedIn(this)
                .collectLatest {
                    _pagingMovie.emit(it)
                    _loadingState.emit(false)
                }
        }
    }
}