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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.itis.myapplication.R
import ru.itis.myapplication.adapter.MoviesAdapter
import ru.itis.myapplication.json_and_gags.Movie
import ru.itis.myapplication.json_and_gags.MovieRepository


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var searchView: android.widget.SearchView
    private var allMovies: List<Movie> = emptyList()

    //Боюсь, за ддос апи
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private fun openFeatureFilmInfoFragment(movieId: Int) {
        val fragment = FeatureFilmInfoFragment.newInstance(movieId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        moviesAdapter = MoviesAdapter(emptyList()) { movieId ->
            val fragment = FeatureFilmInfoFragment.newInstance(movieId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = moviesAdapter

        searchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(MyOnQueryTextListener())

        loadRandomMovies()

    }

    inner class MyOnQueryTextListener : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            searchMovies(query)
            searchView.clearFocus()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }
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
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error loading random movies",
                        Toast.LENGTH_SHORT
                    ).show()
                    println("Error loading random movies: ${e.message}")
                }
            }
        }
    }

    private fun searchMovies(query: String?) {
        if (isSearching) return
        isSearching = true


        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (query != null && query.isNotEmpty()) {
                    val moviesResponse = MovieRepository.searchMovies(query = query)
                    val movies = moviesResponse.docs
                    withContext(Dispatchers.Main) {
                        allMovies = movies
                        moviesAdapter.updateMovies(movies)
                    }
                } else {
                    loadRandomMovies()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error searching movies", Toast.LENGTH_SHORT).show()
                    println("Error searching movies: ${e.message}")
                }
            }
        }
    }

}