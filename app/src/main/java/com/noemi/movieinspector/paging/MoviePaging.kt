package com.noemi.movieinspector.paging

import androidx.paging.PagingData
import com.noemi.movieinspector.model.Movie
import kotlinx.coroutines.flow.Flow

interface MoviePaging {

    fun loadMovies(): Flow<PagingData<Movie>>
}