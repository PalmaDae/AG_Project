package ru.itis.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import org.json.JSONArray
import ru.itis.myapplication.R
import ru.itis.myapplication.json_and_gags.Movie
import ru.itis.myapplication.json_and_gags.MovieRepository

class FeatureFilmInfoFragment : Fragment() {

    private lateinit var imagePoster: ImageView
    private lateinit var textTitle: TextView
    private lateinit var textDescription: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewsContainer: LinearLayout

    private var movieId: Int = -1
    private var posterUrl: String = ""

    companion object {
        private const val ARG_MOVIE_ID = "movie_id"

        fun newInstance(movieId: Int): FeatureFilmInfoFragment {
            return FeatureFilmInfoFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MOVIE_ID, movieId)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_film_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val navInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = navInsets.bottom
            }
            insets
        }


        movieId = arguments?.getInt(ARG_MOVIE_ID) ?: -1
        imagePoster = view.findViewById(R.id.posterIV)
        textTitle = view.findViewById(R.id.titleTV)
        textDescription = view.findViewById(R.id.descriptionTV)
        ratingBar = view.findViewById(R.id.ratingBar)
        reviewsContainer = view.findViewById(R.id.reviewsContainer)

        loadMovieInfo()
        setupActionButton(view)
    }

    private fun loadMovieInfo() {
        if (movieId == -1) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val movie = MovieRepository.getMovieById(movieId.toString())
                withContext(Dispatchers.Main) {
                    updateUI(movie)
                }
            } catch (_: Exception) {

            }
        }
    }

    private fun updateUI(movie: Movie) {
        textTitle.text = movie.name
        textDescription.text = movie.description
        ratingBar.rating = movie.rating?.kp ?: 0f
        posterUrl = movie.poster?.url.orEmpty()

        if (posterUrl.isNotEmpty()) {
            Glide.with(this).load(posterUrl).into(imagePoster)
        }

        showReviews(loadLocalReviews(movieId))
    }

    private fun showReviews(reviews: List<Pair<String, Float>>) {
        reviewsContainer.removeAllViews()

        if (reviews.isEmpty()) {
            reviewsContainer.addView(TextView(requireContext()).apply {
                text = "Отзывов нет)"
                textSize = 16f
                setPadding(0, 16, 0, 16)
                setTextColor(android.graphics.Color.GRAY)
            })
        } else {
            for ((text, rating) in reviews) {
                reviewsContainer.addView(TextView(requireContext()).apply {
                    this.text = "Оценка: $rating\nОтзыв: $text"
                    textSize = 16f
                    setPadding(0, 16, 0, 16)
                })
            }
        }
    }

    private fun loadLocalReviews(movieID: Int): List<Pair<String, Float>> {
        val prefs = requireContext().getSharedPreferences("movie_reviews", android.content.Context.MODE_PRIVATE)
        val raw = prefs.getString("reviews$movieID", "[]") ?: "[]"
        val array = JSONArray(raw)

        return List(array.length()) { i ->
            val obj = array.getJSONObject(i)
            obj.getString("text") to obj.getDouble("rating").toFloat()
        }
    }

    private fun setupActionButton(view: View) {
        view.findViewById<View>(R.id.floatingActionButton).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    ReviewFragment.newInstance(
                        title = textTitle.text.toString(),
                        posterUrl = posterUrl,
                        rating = ratingBar.rating,
                        movieID = movieId
                    )
                )
                .addToBackStack(null)
                .commit()
        }
    }
}