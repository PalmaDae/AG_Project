package ru.itis.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import ru.itis.myapplication.R
import ru.itis.myapplication.adapter.MoviesAdapter
import ru.itis.myapplication.json_and_gags.Movie
import ru.itis.myapplication.json_and_gags.MovieRepository

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var searchView: SearchView

    private var allMovies: List<Movie> = emptyList()
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        moviesAdapter = MoviesAdapter(emptyList()) { movieId ->
            openFeatureFilmInfoFragment(movieId)
        }
        recyclerView.adapter = moviesAdapter


        searchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchMovies(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        loadRandomMovies()
    }

    private fun openFeatureFilmInfoFragment(movieId: Int) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FeatureFilmInfoFragment.newInstance(movieId))
            .addToBackStack(null)
            .commit()
    }

    private fun loadRandomMovies() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val movies = MovieRepository.getRandomMovies(limit = 4)
                withContext(Dispatchers.Main) {
                    allMovies = movies
                    moviesAdapter.updateMovies(movies)
                }
            } catch (e: Exception) {
                showToastOnMain("Ошибка загрузки случайных фильмов")
                println("Error loading random movies: ${e.message}")
            }
        }
    }

    private fun searchMovies(query: String?) {
        if (isSearching) return
        isSearching = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!query.isNullOrEmpty()) {
                    val result = MovieRepository.searchMovies(query)
                    val foundMovies = result.docs
                    withContext(Dispatchers.Main) {
                        allMovies = foundMovies
                        moviesAdapter.updateMovies(foundMovies)
                    }
                } else {
                    loadRandomMovies()
                }
            } catch (e: Exception) {
                showToastOnMain("Ошибка при поиске")
                println("Error searching movies: ${e.message}")
            } finally {
                isSearching = false
            }
        }
    }

    private suspend fun showToastOnMain(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}