package ru.itis.myapplication.fragments

import android.R.attr.text
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import ru.itis.myapplication.R
import ru.itis.myapplication.json_and_gags.Movie
import ru.itis.myapplication.json_and_gags.Poster
import ru.itis.myapplication.json_and_gags.Rating

class FeatureFilmInfoFragment : Fragment(){
    private lateinit var imagePoster: ImageView
    private lateinit var textTitle: TextView
    private lateinit var textDescription: TextView
    private lateinit var ratingBar: RatingBar
    private var movieId: Int = -1
    private var posterUrl: String = ""
    private lateinit var reviewsContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_film_info, container, false)
    }

    companion object {
        private const val ARG_MOVIE_ID = "movie_id"

        fun newInstance(movieId: Int): FeatureFilmInfoFragment {
            val fragment = FeatureFilmInfoFragment()
            val args = Bundle()
            args.putInt(ARG_MOVIE_ID, movieId)
            fragment.arguments = args
            return fragment
        }
    }

    private fun showMovieInfo(movie: Movie) {
        textTitle.text = movie.name
        textDescription.text = movie.description
        ratingBar.rating = movie.rating?.kp ?: 0f

        posterUrl = movie.poster?.url ?: ""

        if (!posterUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(posterUrl)
                .into(imagePoster)
        } else {
            //Тут надо что-то сделать
        }

        val reviews = loadLocalReviews(movieId)

        if (reviews.isEmpty()) {
            val emptyTextView = TextView(requireContext()).apply {
                text = "Отзывов нет)"
                textSize = 16f
                setPadding(0, 16, 0, 16)
                setTextColor(android.graphics.Color.GRAY)
            }
            reviewsContainer.addView(emptyTextView)
        } else {
            for ((text, rating) in reviews) {
                val reviewTextView = TextView(requireContext()).apply {
                    this.text = "Оценка: $rating\nОтзыв: $text"
                    textSize = 16f
                    setPadding(0, 16, 0, 16)
                }
                reviewsContainer.addView(reviewTextView)
            }
        }
    }

    private fun loadMovieInfo() {
        if (movieId == -1) return

        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val movie = ru.itis.myapplication.json_and_gags.MovieRepository.getMovieById(movieId.toString())
                withContext(kotlinx.coroutines.Dispatchers.Main) {
                    showMovieInfo(movie)
                }
            } catch (e: Exception) {
                withContext(kotlinx.coroutines.Dispatchers.Main) {
                }
            }
        }
    }

    private fun actionButtonLogic(view: View) {
        val actionButton: View = view.findViewById(R.id.floatingActionButton)

        actionButton.setOnClickListener {
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

    fun loadLocalReviews(movieID: Int): List<Pair<String, Float>> {
        val sharedReview = requireContext().getSharedPreferences("movie_reviews", android.content.Context.MODE_PRIVATE)
        val reviewsJson = sharedReview.getString("reviews$movieID", "[]")
        val reviewsArray = JSONArray(reviewsJson)
        val result = mutableListOf<Pair<String, Float>>()
        for (i in 0 until reviewsArray.length()) {
            val obj = reviewsArray.getJSONObject(i)
            result.add(obj.getString("text") to obj.getDouble("rating").toFloat())
        }
        return result
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        ViewCompat.setOnApplyWindowInsetsListener(view) { viewToApply, insets ->
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            viewToApply.    updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = navBarInsets.bottom
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

        actionButtonLogic(view)
    }


}