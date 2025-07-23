package ru.itis.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject
import ru.itis.myapplication.R

class ReviewFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    fun loadReviews(movieID: Int): List<Pair<String, Float>> {
        val sharedReview = requireContext().getSharedPreferences("movie_reviews", android.content.Context.MODE_PRIVATE)
        val reviewsJson = sharedReview.getString("reviews$movieID", "[]")
        val reviewsArray = JSONArray(reviewsJson)
        val result = mutableListOf<Pair<String, Float>>()
        for (i in 0 until reviewsArray.length()) {
            val reviewObject = reviewsArray.getJSONObject(i)
            val text = reviewObject.getString("text")
            val rating = reviewObject.getDouble("rating").toFloat()
            result.add(text to rating)
        }
        return result
    }

    private fun reviewButtonLogic(view: View) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("arg_title")
        val posterUrl = arguments?.getString("arg_poster")
        val rating = arguments?.getFloat("arg_rating") ?: 0f
        val movieID = arguments?.getInt("arg_id") ?: return

        val titleTextView = view.findViewById<TextView>(R.id.reviewTitle)
        val posterImageView = view.findViewById<ImageView>(R.id.reviewPoster)
        val ratingBar = view.findViewById<RatingBar>(R.id.reviewRatingBar)

        titleTextView.text = title
        ratingBar.rating = rating

        Glide.with(requireContext())
            .load(posterUrl)
            .into(posterImageView)

        ratingBar.setOnRatingBarChangeListener { _, newRating, _ ->
            Toast.makeText(requireContext(), "Оценка: $newRating", Toast.LENGTH_SHORT).show()
        }

        val reviewButton: View = view.findViewById(R.id.saveReviewButton)

        val reviewEditText = view.findViewById<TextView>(R.id.reviewEditText)
        val reviewRatingBar = view.findViewById<RatingBar>(R.id.reviewRatingBar)


        reviewButton.setOnClickListener {
            val reviewText = reviewEditText.text.toString()
            val movieTitle = title ?: return@setOnClickListener
            val userRating = reviewRatingBar.rating

            if (reviewText.isBlank()) {
                Toast.makeText(requireContext(), "Напишите отзыв)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedReview = requireContext().getSharedPreferences("movie_reviews", android.content.Context.MODE_PRIVATE)

            val oldReviewsJson = sharedReview.getString("reviews$movieID", "[]")
            val oldReviews = JSONArray(oldReviewsJson)

            val newReview = JSONObject()
            newReview.put("text", reviewText)
            newReview.put("rating", userRating)

            oldReviews.put(newReview)

            sharedReview.edit()
                .putString("reviews$movieID", oldReviews.toString())
                .apply()

            Toast.makeText(requireContext(), "Отзыв сохранён!", Toast.LENGTH_SHORT).show()

            parentFragmentManager.popBackStack()
        }

        val reviewsTextView = view.findViewById<TextView>(R.id.reviewsTextView)
        val allReviews = loadReviews(movieID)
        val reviewsDisplay = allReviews.joinToString(separator = "\n\n") { (text, rating) ->
            "Оценка: $rating\nОтзыв: $text"
        }
        reviewsTextView.text = reviewsDisplay
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_POSTER = "arg_poster"
        private const val ARG_RATING = "arg_rating"
        private const val ARG_ID = "arg_id"
        fun newInstance(movieID: Int,title: String, posterUrl: String, rating: Float): ReviewFragment {
            val fragment = ReviewFragment()
            val args = Bundle()
            args.putInt(ARG_ID, movieID)
            args.putString(ARG_TITLE, title)
            args.putString(ARG_POSTER, posterUrl)
            args.putFloat(ARG_RATING, rating)
            fragment.arguments = args
            return fragment
        }
    }
}