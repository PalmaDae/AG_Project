package ru.itis.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.itis.myapplication.R
import ru.itis.myapplication.json_and_gags.Movie
import android.widget.RatingBar

class MoviesAdapter(private var movies: List<Movie>, private val onPosterClick: (movieId: Int) -> Unit) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {


    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        val moviePoster: ImageView = itemView.findViewById(R.id.moviePoster)
        val movieRating: RatingBar = itemView.findViewById(R.id.movieRating)

        init {
            moviePoster.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val movieId = movies[position].id
                    onPosterClick(movieId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.movieTitle.text = movie.name ?: "No Title"

        val posterUrl = movie.poster?.url

        if (posterUrl != null) {
            Glide.with(holder.itemView.context)
                .load(posterUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_error)
                .into(holder.moviePoster)
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.ic_launcher_background)
                .into(holder.moviePoster)
        }

        holder.movieRating.rating = movie.rating?.kp?.toFloat() ?: 0f
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}