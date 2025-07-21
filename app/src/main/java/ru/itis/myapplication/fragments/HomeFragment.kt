package ru.itis.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.itis.myapplication.R
import ru.itis.myapplication.adapter.MoviesAdapter
import ru.itis.myapplication.json_and_gags.Kinopoisk_API
import ru.itis.myapplication.json_and_gags.Movie
import ru.itis.myapplication.json_and_gags.Poster
import ru.itis.myapplication.json_and_gags.Rating

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var moviesAdapter: MoviesAdapter

    private val defaultMovies = listOf(
        Movie(
            name = "Министерство неджентльменских дел",
            description = "",
            poster = Poster(url = "https://ixbt.online/live/images/original/34/10/89/2024/10/11/22c45c2a37.webp"),
            rating = Rating(kp = 3.7f)
        ),
        Movie(
            name = "Доктор Хаус",
            description = "",
            poster = Poster(url = "https://avatars.mds.yandex.net/i?id=2cc128114e73d94993d87a4110b0e622_sr-8283357-images-thumbs&n=13"),
            rating = Rating(kp = 4.4f)
        ),
        Movie(
            name = "Пальма",
            description = "",
            poster = Poster(url = "https://avatars.mds.yandex.net/i?id=3a08e284aeeea94b56e0f1d9284b134c12e90409-10696063-images-thumbs&n=13"),
            rating = Rating(kp = 4.2f)
        ),
        Movie(
            name = "Красный шёлк",
            description = "",
            poster = Poster(url = "https://avatars.dzeninfra.ru/get-zen_doc/271828/pub_67bc23c4b981601bb8946b6b_67bc2642904747545d4f9552/scale_1200"),
            rating = Rating(kp = 3.7f)
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        moviesAdapter = MoviesAdapter(emptyList())
        recyclerView.adapter = moviesAdapter

        loadDefaultMovies()

    }

    private fun loadDefaultMovies() {
        moviesAdapter.updateMovies(defaultMovies)
    }


}

