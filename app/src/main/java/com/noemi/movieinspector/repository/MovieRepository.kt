package com.noemi.movieinspector.repository

import com.noemi.movieinspector.model.Review
import com.noemi.movieinspector.model.Trailer
import com.noemi.movieinspector.room.MovieEntity
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun loadTrailers(id: Int): Flow<List<Trailer>>

    fun loadReviews(id: Int): Flow<List<Review>>

    fun observeMovies(): Flow<List<MovieEntity>>

    suspend fun getMovie(id: Int): MovieEntity

    suspend fun insertMovie(movie: MovieEntity)
}