package com.usama.moviesearchapp.data.models.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.usama.moviesearchapp.data.models.remote.Movie

@Entity(tableName = "bookmarked_movies")
data class BookmarkedMovieEntity(
    @PrimaryKey val imdbID: String,
    val Poster: String,
    val Title: String,
    val Type: String,
    val Year: String
){
    fun toMovie(): Movie {
        return Movie(
            Poster = this.Poster,
            Title = this.Title,
            Type = this.Type,
            Year = this.Year,
            imdbID = this.imdbID,
            isBookmarked = true
        )
    }
}
