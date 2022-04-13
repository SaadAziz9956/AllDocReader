package com.example.alldocreader.ui.fragments.home.fragments.allfiles

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alldocreader.databinding.FragmentAllFilesBinding
import com.example.alldocreader.helper.Constants.ALL
import com.example.alldocreader.helper.EventsHandler
import com.example.alldocreader.helper.RequestPermission
import com.example.alldocreader.room.entity.FilesEntity
import com.example.alldocreader.ui.bottomsheetdialog.MoreDialogFragment
import com.example.alldocreader.ui.fragments.home.fragments.SharedViewModel
import com.example.alldocreader.ui.fragments.home.fragments.allfiles.adapter.AllFilesAdapter
import com.example.alldocreader.ui.fragments.home.fragments.allfiles.viewmodel.AllFilesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class AllFilesFragment : Fragment(), MoreDialogFragment.CallBack {

    private var _binding: FragmentAllFilesBinding? = null
    private val binding get() = _binding

    private var _dialog: MoreDialogFragment? = null
    private val dialog get() = _dialog

    private var _allFilesAdapter: AllFilesAdapter? = null
    private val allFilesAdapter get() = _allFilesAdapter

    private val viewModel by viewModels<AllFilesViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    var type = ALL

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAllFilesBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDialog()

        startCollecting()

        initRecycler()

        onClick()
    }

    private fun initDialog() {
        _dialog = MoreDialogFragment()
    }


    private fun onClick() {
        binding?.apply {

            permissionDialog.btnPermission.setOnClickListener {

                RequestPermission.getStoragePermission(requireActivity())

            }

        }
    }

    private fun startCollecting() {

        lifecycleScope.launchWhenCreated {

            sharedViewModel.type
                .collectLatest { type ->
                    binding?.loading?.visibility = View.VISIBLE
                    binding?.recyclerView?.visibility = View.GONE
                    this@AllFilesFragment.type = type
                    delay(1500)
                    viewModel.getFileFromDb(type)
                }

        }

        lifecycleScope.launchWhenCreated {
            viewModel.event
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { event ->

                    when (event) {

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

                        is EventsHandler.Files -> {
                            event.files
                                ?.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                                ?.collectLatest { list ->
                                    Timber.d("setRecyclerData")
                                    setRecyclerData(list)
                                }
                        }

                        else -> Unit

                    }

                }
        }

        lifecycleScope.launchWhenCreated {
            sharedViewModel.event.collectLatest { event ->
                Timber.d("permission")

                when (event) {

                    is EventsHandler.Permission -> {

                        when (event.permission) {

                            true -> {
                                Timber.d("true")

                                binding?.mainLayout?.visibility = View.VISIBLE

                                binding?.permissionLayout?.visibility = View.GONE

                                viewModel.sendFileRequest()

                                binding?.loading?.visibility = View.VISIBLE

                                binding?.notFound?.visibility = View.GONE

                                delay(1500)

                                viewModel.getFileFromDb(type)

                            }

                            false -> {
                                Timber.d("false")

                                binding?.permissionLayout?.visibility = View.VISIBLE

                                binding?.mainLayout?.visibility = View.GONE
                            }

                            else -> Unit
                        }

                        viewModel.resetValues()

                    }
                    else -> Unit
                }

            }
        }

    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.checkPermission()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler() {

        _allFilesAdapter = AllFilesAdapter(

            onClick = { pdfFile ->
                viewModel.sendIntentRequest(pdfFile)
            },

            onClickMoreOptions = { file ->
                dialog?.show(childFragmentManager, "BottomSheetDialog")
                dialog?.setData(file, false)
            }

        )
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = allFilesAdapter
        }
    }

    private suspend fun setRecyclerData(list: List<FilesEntity>) {

        allFilesAdapter?.setData(list)

        setVisibility(list)

    }

    private suspend fun setVisibility(list: List<FilesEntity>) {

        delay(500)

        binding?.loading?.visibility = View.GONE

        if (list.isNotEmpty()) {

            binding?.recyclerView?.visibility = View.VISIBLE
            binding?.notFound?.visibility = View.GONE

        } else {

            binding?.recyclerView?.visibility = View.GONE
            binding?.notFound?.visibility = View.VISIBLE

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _dialog = null

        binding?.recyclerView?.adapter = null

        _allFilesAdapter = null

        _binding = null

    }

    override fun renameFile(renamed: Boolean, file: FilesEntity) {
        lifecycleScope.launch {
            sharedViewModel.renameFile(renamed, file, false)
        }
    }

    override fun deleteFile(deleted: Boolean, file: FilesEntity) {
        lifecycleScope.launch {
            sharedViewModel.deleteFile(deleted, file)
        }
    }

    override fun isFavourite(file: FilesEntity) {
        lifecycleScope.launch {
            sharedViewModel.setFavourite(file)
        }
    }

}