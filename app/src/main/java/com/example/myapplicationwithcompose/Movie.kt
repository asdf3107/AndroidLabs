package com.example.myapplicationwithcompose

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Movie(
    val title: String,
    val releaseDate: String,
    val imdbRating: Double
)

interface MovieApi {
    @GET("movies/animation")
    suspend fun getMovies(): List<Movie>
}

object RetrofitClient {
    val api: MovieApi = Retrofit.Builder()
        .baseUrl("https://api.sampleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MovieApi::class.java)
}
