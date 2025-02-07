package com.usama.moviesearchapp.ui.movie

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.usama.moviesearchapp.R
import com.usama.moviesearchapp.data.models.remote.Movie
import com.usama.moviesearchapp.databinding.FragmentMovieBinding
import com.usama.moviesearchapp.ui.common.adapter.MovieAdapter
import com.usama.moviesearchapp.ui.common.adapter.MovieLoadStateAdapter
import com.usama.moviesearchapp.ui.common.viewmodel.SharedMovieViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFragment : Fragment(R.layout.fragment_movie),
    MovieAdapter.OnItemClickListener {

    private val sharedViewModel: SharedMovieViewModel by activityViewModels()

    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!
    private var adapter: MovieAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMovieBinding.bind(view)

        adapter = MovieAdapter(this)

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.itemAnimator = null
            recyclerView.adapter = adapter?.withLoadStateHeaderAndFooter(
                header = MovieLoadStateAdapter { adapter?.retry() },
                footer = MovieLoadStateAdapter { adapter?.retry() }
            )
            buttonRetry.setOnClickListener { adapter?.retry() }

            // Initialize SwipeRefreshLayout
            swipeRefreshLayout.setOnRefreshListener {
                sharedViewModel.refreshMovies()
            }
        }

        sharedViewModel.movies.observe(viewLifecycleOwner) { pagingData ->
            adapter?.submitData(lifecycle, pagingData)
        }

        adapter?.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter?.itemCount!! < 1
                ) {
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }

                // Hide the SwipeRefreshLayout loading indicator
                swipeRefreshLayout.isRefreshing = loadState.source.refresh is LoadState.Loading
            }
        }

        addMenuToTopBar()
    }

    override fun onItemClick(movie: Movie) {
        val action = MovieFragmentDirections.actionMovieFragmentToDetailsFragment(movie)
        findNavController().navigate(action)
    }

    override fun onBookmarkClick(movie: Movie) {
        sharedViewModel.toggleBookmark(movie)
    }

    private fun addMenuToTopBar(){
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                // Add menu items here
                menuInflater.inflate(R.menu.menu_movie, menu)
                requireActivity().invalidateOptionsMenu()

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {

                        if (query != null) {
                            binding.recyclerView.scrollToPosition(0)
                            sharedViewModel.searchMovies(query)
                            searchView.clearFocus()
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}