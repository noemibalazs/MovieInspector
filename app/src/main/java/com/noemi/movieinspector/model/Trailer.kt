package com.noemi.movieinspector.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Trailers(
    @SerialName("results") val trailers: List<Trailer>
)

@Serializable
data class Trailer(
    @SerialName("key")
    val key: String,

    @SerialName("name")
    val name: String
)
