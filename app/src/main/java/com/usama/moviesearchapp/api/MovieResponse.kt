package com.usama.moviesearchapp.api

import com.usama.moviesearchapp.data.Movie


data class MovieResponse(
    val Response: String,
    val Search: List<Movie>?,
    val totalResults: String
)