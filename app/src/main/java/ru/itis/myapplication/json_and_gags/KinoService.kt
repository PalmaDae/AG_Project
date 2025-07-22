package ru.itis.myapplication.json_and_gags

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header

interface KinoService {

    @GET("v1.4/movie/{id}")
    suspend fun getMovieById(
        @retrofit2.http.Path("id") id: String,
        @Header("X-API-KEY") apiKey: String
    ): Movie

    @GET("v1.4/movie/search")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Header("X-API-KEY") apiKey: String = SecretStrings.API_key
    ): MoviesResponse
}