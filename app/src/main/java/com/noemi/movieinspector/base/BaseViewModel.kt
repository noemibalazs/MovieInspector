package com.noemi.movieinspector.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.repository.MovieRepository
import com.noemi.movieinspector.utils.toEntity
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<T : Any>(
    application: Application,
    private val repository: MovieRepository
) : AndroidViewModel(application) {

    protected val context
        get() = getApplication<Application>()

    abstract val loadingState: StateFlow<Boolean>
    abstract val errorState: StateFlow<String>
    abstract val outcomeState: StateFlow<T>

    fun saveMovie(movie: Movie) {
        viewModelScope.launch {
            val entity = movie.toEntity(context)
            repository.insertMovie(entity)
        }
    }
}