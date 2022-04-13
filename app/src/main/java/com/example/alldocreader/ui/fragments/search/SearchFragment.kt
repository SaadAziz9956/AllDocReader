package com.example.alldocreader.ui.fragments.search

import android.app.ActivityOptions
import android.app.SearchManager
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alldocreader.databinding.FragmentSearchBinding
import com.example.alldocreader.helper.EventsHandler
import com.example.alldocreader.room.entity.FilesEntity
import com.example.alldocreader.ui.fragments.search.adapter.SearchAdapter
import com.example.alldocreader.ui.fragments.search.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SearchFragment : Fragment(), Filterable {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding

    private var _searchAdapter: SearchAdapter? = null
    private val searchAdapter get() = _searchAdapter

    private var list = mutableListOf<FilesEntity>()

    private var filterList = mutableListOf<FilesEntity>()

    private val viewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setSearchView()

        initRecycler()

        onClick()

        startCollecting()

    }

    private fun setSearchView() {
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager

        binding?.apply {

            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))

            searchView.setIconifiedByDefault(false)

            searchView.isQueryRefinementEnabled = true

            searchView.requestFocus(1)

        }

    }

    private fun startCollecting() {
        lifecycleScope.launchWhenCreated {
            viewModel.event
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { event ->

                    when(event) {

                        is EventsHandler.MoveForward -> {

                            val options =
                                ActivityOptions.makeCustomAnimation(
                                    context,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                                )

                            startActivity(event.intent, options.toBundle())

                            viewModel.resetValues()

                        }

                        else -> Unit

                    }

                }
        }
    }

    private fun onClick() {

        binding?.searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                binding?.recyclerView?.visibility = View.GONE

                binding?.progressBar?.visibility = View.VISIBLE

                viewModel.getFileFromDB()
                    .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                    .onEach {
                        this@SearchFragment.list = it as MutableList<FilesEntity>
                        filter.filter(query)
                        delay(1000)
                        binding?.progressBar?.visibility = View.GONE
                        binding?.recyclerView?.visibility = View.VISIBLE
                    }
                    .launchIn(lifecycleScope)

                return true

            }

            override fun onQueryTextChange(newText: String): Boolean {

                binding?.recyclerView?.visibility = View.GONE

                return false

            }
        })

    }

    private fun initRecycler() {
        _searchAdapter = SearchAdapter(
            onClick = { pdfFile ->
                viewModel.sendIntentRequest(pdfFile)
            })
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val tempList = viewModel.filterList(charSequence, list)
                val filterResults = FilterResults()
                filterResults.values = tempList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filterList = filterResults.values as MutableList<FilesEntity>
                setRecyclerData(filterList)
            }
        }
    }

    private fun setRecyclerData(list: List<FilesEntity>) {
        searchAdapter?.setData(list)
    }

    override fun onResume() {
        super.onResume()
        openKeyBoard()
    }

    private fun openKeyBoard() {
        binding?.searchView?.onActionViewExpanded()
        if (binding?.searchView?.requestFocus() == true) {
            val inputMethodManager =
                (requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            inputMethodManager.showSoftInput(binding?.searchView, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.recyclerView?.adapter = null

        _searchAdapter = null

        _binding = null
    }
}