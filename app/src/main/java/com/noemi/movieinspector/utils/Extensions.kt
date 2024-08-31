package com.noemi.movieinspector.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.noemi.movieinspector.model.Movie
import com.noemi.movieinspector.room.MovieEntity
import com.noemi.movieinspector.screens.details.MovieDetailsActivity

fun Movie.toEntity(context: Context): MovieEntity =
    MovieEntity(id = id, title = title, description = description, releaseDate = releaseDate, rating = rating, posterPath = getPoster(context))

fun Movie.getPoster(context: Context): String = when (posterPath == null) {
    true -> context.getDrawableUri()
    else -> posterPath.getMoviePoster()
}

fun Context.getDrawableUri(): String {
    return Uri.parse("android.resource://" + this.packageName + "drawable/placeholder").toString()
}

fun String.getMoviePoster(): String {
    return POSTER_URL + this
}

fun MovieEntity.toMovie(): Movie {
    val path = when (posterPath.startsWith(POSTER_URL)) {
        true -> posterPath.drop(31)
        else -> posterPath
    }
    return Movie(
        id = id, title = title, description = description, releaseDate = releaseDate, rating = rating, posterPath = path
    )
}

fun Context.toMovieDetails(movieId: Int) {
    val intent = Intent(this, MovieDetailsActivity::class.java)
    val bundle = Bundle()
    bundle.putInt(KEY_MOVIE_ID, movieId)
    intent.putExtras(bundle)
    startActivity(intent)
}

fun String.getMovieYoutubePath(): String {
    return YOUTUBE_PATH + this
}

fun String.getYoutubeScreenShot(): String {
    return YOUTUBE_START + this + YOUTUBE_END
}