package com.noemi.movieinspector.network

import com.noemi.movieinspector.model.Movies
import com.noemi.movieinspector.model.Reviews
import com.noemi.movieinspector.model.Trailers
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    @GET("movie/top_rated")
    suspend fun loadTopRatedMovies(@Query("api_key") key: String, @Query("page") page: Int): Movies

    @GET("movie/popular")
    suspend fun loadPopularMovies(@Query("api_key") key: String, @Query("page") page: Int): Movies

    @GET("movie/{id}/videos")
    suspend fun loadTrailers(@Path("id") id: Int, @Query("api_key") key: String): Trailers

    @GET("movie/{id}/reviews")
    suspend fun loadReviews(@Path("id") id: Int, @Query("api_key") key: String): Reviews
}