package com.noemi.movieinspector.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reviews(
    @SerialName("results") val reviews: List<Review>
)

@Serializable
data class Review(
    @SerialName("author")
    val author: String,

    @SerialName("content")
    val content: String
)
