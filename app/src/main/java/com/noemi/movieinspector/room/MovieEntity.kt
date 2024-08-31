package com.noemi.movieinspector.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noemi.movieinspector.utils.MOVIE_TABLE

@Entity(tableName = MOVIE_TABLE)
data class MovieEntity(

    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val releaseDate: String,
    val rating: Double,
    val posterPath: String
)
