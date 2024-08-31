package com.noemi.movieinspector.paging.popular

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.noemi.movieinspector.BuildConfig
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.network.MovieService
import javax.inject.Inject

class PopularPaging @Inject constructor(
    private val service: MovieService
) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val position = params.key ?: 1
            val response = service.loadPopularMovies(BuildConfig.MOVIE_KEY, position)

            when (response.movies.isNotEmpty()) {
                true -> LoadResult.Page(
                    data = response.movies,
                    prevKey = if (position == 1) null else position.minus(1),
                    nextKey = if (position == response.totalPages) null else position.plus(1)
                )

                else -> LoadResult.Error(Throwable("No popular movie results found."))
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}