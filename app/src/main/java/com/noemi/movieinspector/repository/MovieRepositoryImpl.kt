package com.noemi.movieinspector.repository

import com.noemi.movieinspector.BuildConfig
import com.noemi.movieinspector.model.Review
import com.noemi.movieinspector.model.Trailer
import com.noemi.movieinspector.network.MovieService
import com.noemi.movieinspector.room.MovieDAO
import com.noemi.movieinspector.room.MovieEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val service: MovieService,
    private val dispatcher: CoroutineDispatcher,
    private val movieDAO: MovieDAO
) : MovieRepository {

    override fun loadReviews(id: Int): Flow<List<Review>> = flow {
        emit(service.loadReviews(id, BuildConfig.MOVIE_KEY).reviews)
    }.flowOn(dispatcher)

    override fun loadTrailers(id: Int): Flow<List<Trailer>> = flow {
        emit(service.loadTrailers(id, BuildConfig.MOVIE_KEY).trailers)
    }.flowOn(dispatcher)

    override fun observeMovies(): Flow<List<MovieEntity>> = movieDAO.observeMovies()

    override suspend fun getMovie(id: Int): MovieEntity = movieDAO.getMovie(id)

    override suspend fun insertMovie(movie: MovieEntity) = movieDAO.insertMovie(movie)
}