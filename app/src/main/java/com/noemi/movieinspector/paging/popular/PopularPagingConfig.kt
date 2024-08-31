package com.noemi.movieinspector.paging.popular

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.paging.MoviePaging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PopularPagingConfig @Inject constructor(
    private val paging: PopularPaging,
    private val dispatcher: CoroutineDispatcher
) : MoviePaging {

    override fun loadMovies(): Flow<PagingData<Movie>> = Pager(
        config = PagingConfig(pageSize = 10, maxSize = 30),
        pagingSourceFactory = { paging }
    ).flow.flowOn(dispatcher)
}