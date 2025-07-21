package ru.itis.myapplication.json_and_gags

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Kinopoisk_API {
    private val apiKey = SecretStrings.API_key

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.kinopoisk.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: KinopoiskApiService = retrofit.create(KinopoiskApiService::class.java)

    fun getMovies(
        year: Int? = null,
        genre: String? = null,
        ratingRange: String? = null
    ) = service.getMovies(year, genre, ratingRange, apiKey)

    fun getMovies2(
        year: Int? = null,
        genre: String? = null,
        ratingRange: String? = null,
        limit: Int? = null
    ): MoviesResponse = service.getMovies2(year, genre, ratingRange, limit, apiKey)

    fun searchMovies(
        query: String,
        page: Int = 1,
        limit: Int = 10
    ): List<Movie> = service.searchMovies(query = query, page = page, limit = limit, apiKey = apiKey).movies

}