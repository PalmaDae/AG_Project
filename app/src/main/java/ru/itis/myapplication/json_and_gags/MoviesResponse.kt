package ru.itis.myapplication.json_and_gags

data class MoviesResponse (
    val docs: List<Movie>
)

data class Movie(
    val name: String,
    val description: String?,
    val poster: Poster?,
    val rating: Rating?
)

data class Poster(
    val url: String?
)

data class Rating(
    val kp: Float?
)