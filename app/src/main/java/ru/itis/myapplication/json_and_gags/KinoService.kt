package ru.itis.myapplication.json_and_gags

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header

interface KinopoiskApiService {
    @GET("v1.4/movie")
    fun getMovies(
        @Query("year") year: Int? = null,
        @Query("genres.name") genre: String? = null,
        @Query("rating.imdb") ratingRange: String? = null,
        @Header("X-API-KEY") apiKey: String = SecretStrings.API_key
    ): Call<MoviesResponse>
}