package com.noemi.movieinspector.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.noemi.movieinspector.utils.MOVIE_TABLE
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDAO {

    @Query("SELECT * FROM $MOVIE_TABLE")
    fun observeMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM $MOVIE_TABLE WHERE id = :id")
    suspend fun getMovie(id: Int): MovieEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)
}