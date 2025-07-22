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
import ru.itis.myapplication.R

class ReviewFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("arg_title")
        val posterUrl = arguments?.getString("arg_poster")
        val rating = arguments?.getFloat("arg_rating") ?: 0f

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
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_POSTER = "arg_poster"
        private const val ARG_RATING = "arg_rating"

        fun newInstance(title: String, posterUrl: String, rating: Float): ReviewFragment {
            val fragment = ReviewFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_POSTER, posterUrl)
            args.putFloat(ARG_RATING, rating)
            fragment.arguments = args
            return fragment
        }
    }
}