package ru.itis.myapplication.json_and_gags

data class SearchMoviesResponse(
    val movies: List<Movie>,
    val total: Int,
    val page: Int,
    val pages: Int
)