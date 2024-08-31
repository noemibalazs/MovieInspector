package com.noemi.movieinspector.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movies(
    @SerialName("results") val movies: List<Movie>,
    @SerialName("total_pages") val totalPages: Int
)

@Serializable
data class Movie(

    @SerialName("id")
    val id: Int = 0,

    @SerialName("title")
    val title: String = "",

    @SerialName("overview")
    val description: String = "",

    @SerialName("release_date")
    val releaseDate: String = "",

    @SerialName("vote_average")
    val rating: Double = 0.0,

    @SerialName("poster_path")
    val posterPath: String? = null
)
