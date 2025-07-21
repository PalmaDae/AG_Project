package ru.itis.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.itis.myapplication.R
import ru.itis.myapplication.json_and_gags.Movie
import ru.itis.myapplication.json_and_gags.Poster
import ru.itis.myapplication.json_and_gags.Rating

class FeatureFilmInfoFragment : Fragment(){
    private lateinit var imagePoster: ImageView
    private lateinit var textTitle: TextView
    private lateinit var textDescription: TextView
    private lateinit var ratingBar: RatingBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_film_info, container, false)
    }

    private fun showMovieInfo(movie: Movie) {
        textTitle.text = movie.name
        textDescription.text = movie.description
        ratingBar.rating = movie.rating?.kp ?: 0f

        val posterUrl = movie.poster?.url

        if (!posterUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(posterUrl)
                .into(imagePoster)
        } else {
            //Тут надо что-то сделать
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imagePoster = view.findViewById(R.id.posterIV)
        textTitle = view.findViewById(R.id.titleTV)
        textDescription = view.findViewById(R.id.descriptionTV)
        ratingBar = view.findViewById(R.id.ratingBar)

        val movie = Movie(
            name = "Dexter",
            description = "He's smart. He's lovable. He's Dexter Morgan, America's favorite serial killer, who spends his days solving crimes and his nights committing them.",
            poster = Poster(url = "https://avatars.mds.yandex.net/i?id=354e9b12d76c9e5c3a5ce6c6da6099a7_l-7004933-images-thumbs&n=13"),
            rating = Rating(kp = 4.5f)
        )

        showMovieInfo(movie)
    }
}