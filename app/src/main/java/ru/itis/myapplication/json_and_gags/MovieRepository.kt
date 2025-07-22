package ru.itis.myapplication.json_and_gags

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

object MovieRepository {
    private val apiKey = SecretStrings.API_key

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.kinopoisk.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: KinoService = retrofit.create(KinoService::class.java)

    suspend fun getRandomMovies(limit: Int = 4): List<Movie> {

        val allMovies = mutableListOf<Movie>()

        for (page in 1..3) {
            try {
                val movies = service.searchMovies("", page, 100, apiKey).docs
                allMovies.addAll(movies)
            } catch (e: Exception) {
                println("Error fetching page $page: ${e.message}")
            }
        }

        val randomMovies = allMovies.shuffled(Random.Default).take(limit)
        return randomMovies
    }

    suspend fun searchMovies(
        query: String,
        page: Int = 1,
        limit: Int = 100
    ): MoviesResponse {
        return service.searchMovies(query, page, limit, apiKey)
    }

    suspend fun getMovieById(id: String): Movie {
        return service.getMovieById(id, apiKey)
    }

}