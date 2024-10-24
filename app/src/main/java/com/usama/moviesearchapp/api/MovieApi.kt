package com.usama.moviesearchapp.api

import com.usama.moviesearchapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    companion object {
        const val BASE_URL = "https://www.omdbapi.com"
        const val API_KEY = BuildConfig.ACCESS_KEY
    }

    @GET("/")
    suspend fun searchMovies(
        @Query("s") query: String,
        @Query("page") page: Int,
        @Query("apikey") apiKey: String = API_KEY
    ): MovieResponse

}