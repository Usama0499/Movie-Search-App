package com.usama.moviesearchapp.data.models.remote


data class MovieResponse(
    val Response: String,
    val Search: List<Movie>?,
    val totalResults: String
)