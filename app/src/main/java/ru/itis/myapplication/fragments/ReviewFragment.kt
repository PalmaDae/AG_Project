package ru.itis.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject
import ru.itis.myapplication.R

class ReviewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_review, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(ARG_TITLE)
        val posterUrl = arguments?.getString(ARG_POSTER)
        val rating = arguments?.getFloat(ARG_RATING) ?: 0f
        val movieID = arguments?.getInt(ARG_ID) ?: return

        val titleTextView = view.findViewById<TextView>(R.id.reviewTitle)
        val posterImageView = view.findViewById<ImageView>(R.id.reviewPoster)
        val ratingBar = view.findViewById<RatingBar>(R.id.reviewRatingBar)
        val reviewEditText = view.findViewById<EditText>(R.id.reviewEditText)
        val saveButton = view.findViewById<Button>(R.id.saveReviewButton)
        val reviewsTextView = view.findViewById<TextView>(R.id.reviewsTextView)

        titleTextView.text = title
        ratingBar.rating = rating

        Glide.with(requireContext())
            .load(posterUrl)
            .into(posterImageView)

        ratingBar.setOnRatingBarChangeListener { _, newRating, _ ->
            Toast.makeText(requireContext(), getString(R.string.rate) + newRating, Toast.LENGTH_SHORT).show()
        }

        saveButton.setOnClickListener {
            saveReview(movieID, reviewEditText.text.toString(), ratingBar.rating)
        }

        val allReviews = loadReviews(movieID)
        reviewsTextView.text = formatReviews(allReviews)
    }

    private fun saveReview(movieID: Int, text: String, rating: Float) {
        if (text.isBlank()) {
            Toast.makeText(requireContext(), getString(R.string.review_write), Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = requireContext().getSharedPreferences("movie_reviews", android.content.Context.MODE_PRIVATE)
        val oldJson = prefs.getString("reviews$movieID", "[]")
        val array = JSONArray(oldJson)

        val newReview = JSONObject().apply {
            put("text", text)
            put("rating", rating)
        }

        array.put(newReview)

        prefs.edit()
            .putString("reviews$movieID", array.toString())
            .apply()

        Toast.makeText(requireContext(), getString(R.string.review_saved), Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    private fun loadReviews(movieID: Int): List<Pair<String, Float>> {
        val prefs = requireContext().getSharedPreferences("movie_reviews", android.content.Context.MODE_PRIVATE)
        val raw = prefs.getString("reviews$movieID", "[]") ?: "[]"
        val array = JSONArray(raw)

        return List(array.length()) { i ->
            val obj = array.getJSONObject(i)
            obj.getString("text") to obj.getDouble("rating").toFloat()
        }
    }

    private fun formatReviews(reviews: List<Pair<String, Float>>): String {
        return if (reviews.isEmpty()) {
            getString(R.string.no_review)
        } else {
            reviews.joinToString("\n\n") { (text, rating) ->
                getString(R.string.rate) + "\n" + getString(R.string.review) + text
            }
        }
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_POSTER = "arg_poster"
        private const val ARG_RATING = "arg_rating"
        private const val ARG_ID = "arg_id"

        fun newInstance(movieID: Int, title: String, posterUrl: String, rating: Float): ReviewFragment {
            return ReviewFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, movieID)
                    putString(ARG_TITLE, title)
                    putString(ARG_POSTER, posterUrl)
                    putFloat(ARG_RATING, rating)
                }
            }
        }
    }
}