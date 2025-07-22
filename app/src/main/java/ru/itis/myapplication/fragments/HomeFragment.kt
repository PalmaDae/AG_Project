package ru.itis.myapplication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar
    private var allMovies: List<Movie> = emptyList()


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
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        moviesAdapter = MoviesAdapter(emptyList())
        recyclerView.adapter = moviesAdapter

        searchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(MyOnQueryTextListener())

        progressBar = view.findViewById(R.id.progressBar)

        searchView.isIconified = false
        searchView.requestFocusFromTouch()

        showLoading(true)
        loadRandomMovies()

    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
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
            } finally {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                }
            }
        }
    }

    private fun searchMovies(query: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    showLoading(true)
                }
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
                Log.e("HomeFragment", "Error searching movies: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error searching movies: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                }
            }
        }
    }
}