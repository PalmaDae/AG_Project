package ru.itis.myapplication.json_and_gags

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MovieRepository {
    private val apiKey = SecretStrings.API_key

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.kinopoisk.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: KinopoiskApiService = retrofit.create(KinopoiskApiService::class.java)

    suspend fun searchMovies(
        query: String,
        page: Int = 1,
        limit: Int = 100
    ): List<Movie> {
        return service.searchMovies(query, page, limit, apiKey).docs
    }

    suspend fun getMovieById(id: String): Movie {
        return service.getMovieById(id, apiKey)
    }
}