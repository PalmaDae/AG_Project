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

    @GET("v1.4/movie")
    fun getMovies2(
        @Query("year") year: Int? = null,
        @Query("genres.name") genre: String? = null,
        @Query("rating.imdb") ratingRange: String? = null,
        @Query("limit") limit: Int? = null,
        @Header("X-API-KEY") apiKey: String = SecretStrings.API_key
    ): MoviesResponse

    @GET("v1.4/movie/search")
    fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Header("X-API-KEY") apiKey: String = SecretStrings.API_key
    ): SearchMoviesResponse
}