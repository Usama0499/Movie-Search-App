package com.usama.moviesearchapp.ui.bookmark

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.usama.moviesearchapp.R
import com.usama.moviesearchapp.data.models.remote.Movie
import com.usama.moviesearchapp.databinding.FragmentMovieBinding
import com.usama.moviesearchapp.ui.common.viewmodel.SharedMovieViewModel
import com.usama.moviesearchapp.ui.common.adapter.MovieAdapter
import com.usama.moviesearchapp.ui.common.adapter.MovieLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkFragment : Fragment(R.layout.fragment_movie),
    MovieAdapter.OnItemClickListener {

    private val sharedViewModel: SharedMovieViewModel by activityViewModels()

    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMovieBinding.bind(view)

        val adapter = MovieAdapter(this)

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.itemAnimator = null
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = MovieLoadStateAdapter { adapter.retry() },
                footer = MovieLoadStateAdapter { adapter.retry() }
            )
            buttonRetry.setOnClickListener { adapter.retry() }

            // Initialize SwipeRefreshLayout
            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = false
            }
        }


        sharedViewModel.bookmarkedMovies.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
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

    }

    override fun onItemClick(movie: Movie) {
        val action = BookmarkFragmentDirections.actionBookmarkFragmentToDetailsFragment(movie)
        findNavController().navigate(action)
    }

    override fun onBookmarkClick(movie: Movie) {
        sharedViewModel.toggleBookmark(movie)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}