package com.example.alldocreader.ui.fragments.home.fragments.recent

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.alldocreader.databinding.FragmentRecentBinding
import com.example.alldocreader.helper.EventsHandler
import com.example.alldocreader.helper.RequestPermission
import com.example.alldocreader.room.entity.FilesEntity
import com.example.alldocreader.ui.bottomsheetdialog.MoreDialogFragment
import com.example.alldocreader.ui.fragments.home.fragments.SharedViewModel
import com.example.alldocreader.ui.fragments.home.fragments.allfiles.adapter.AllFilesAdapter
import com.example.alldocreader.ui.fragments.home.fragments.recent.viewmodel.RecentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RecentFragment : Fragment(), MoreDialogFragment.CallBack {

    private var _binding: FragmentRecentBinding? = null
    private val binding get() = _binding

    private var _dialog: MoreDialogFragment? = null
    private val dialog get() = _dialog

    private var _recentAdapter: AllFilesAdapter? = null
    private val recentAdapter get() = _recentAdapter

    private val viewModel by viewModels<RecentViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRecentBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDialog()

        initRecycler()

        startCollecting()

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

            viewModel.getRecentFiles()
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { list ->

                    setRecyclerData(list)

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

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecycler() {


        _recentAdapter = AllFilesAdapter(

            onClick = { pdfFile ->
                viewModel.sendIntentRequest(pdfFile)
            },

            onClickMoreOptions = { pdfFile ->
                dialog?.show(childFragmentManager, "BottomSheetDialog")
                dialog?.setData(pdfFile, false)
            },
        )
        recentAdapter?.notifyDataSetChanged()
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentAdapter
        }
    }

    private fun setRecyclerData(list: List<FilesEntity>) {
        recentAdapter?.setData(list)
        setVisibility(list)
    }

    private fun setVisibility(list: List<FilesEntity>) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding?.loading?.visibility = View.GONE
            if (list.isNotEmpty()) {
                binding?.recyclerView?.visibility = View.VISIBLE
                binding?.notFound?.visibility = View.GONE
            } else {
                binding?.recyclerView?.visibility = View.GONE
                binding?.notFound?.visibility = View.VISIBLE
            }
        }, 1200)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null

        binding?.recyclerView?.adapter = null

        _recentAdapter = null

        _dialog = null

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