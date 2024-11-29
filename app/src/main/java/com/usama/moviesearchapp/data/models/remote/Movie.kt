package com.usama.moviesearchapp.data.models.remote

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val Poster: String,
    val Title: String,
    val Type: String,
    val Year: String,
    val imdbID: String,
    val isBookmarked: Boolean = false
) : Parcelable