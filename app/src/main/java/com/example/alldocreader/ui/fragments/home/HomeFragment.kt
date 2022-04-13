package com.example.alldocreader.ui.fragments.home

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.alldocreader.R
import com.example.alldocreader.ui.fragments.home.adapter.ViewPagerAdapter
import com.example.alldocreader.ui.fragments.home.fragments.allfiles.AllFilesFragment
import com.example.alldocreader.ui.fragments.home.fragments.favourite.FavouriteFragment
import com.example.alldocreader.ui.fragments.home.fragments.recent.RecentFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

import com.example.alldocreader.databinding.HomeFragmentBinding
import com.example.alldocreader.helper.Constants.ALL
import com.example.alldocreader.helper.Constants.DOC
import com.example.alldocreader.helper.Constants.PDF
import com.example.alldocreader.helper.Constants.PPT
import com.example.alldocreader.helper.Constants.XLS
import com.example.alldocreader.ui.fragments.home.fragments.SharedViewModel
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding

    private var _viewPagerAdapter: ViewPagerAdapter? = null
    private val viewPagerAdapter get() = _viewPagerAdapter

    private var _tabLayoutMediator: TabLayoutMediator? = null
    private val tabLayoutMediator get() = _tabLayoutMediator

    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private val list = mutableListOf<PowerMenuItem>()
    private lateinit var powerMenu: PowerMenu

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = HomeFragmentBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.typeMS.value = ALL

        setupViewPager()

        setupMenu()

        clickListener()

    }

    private fun clickListener() {
        binding?.logo?.setOnClickListener {
            powerMenu.showAsDropDown(it)
        }
    }

    private fun setupMenu() {
        powerMenu = PowerMenu.Builder(requireContext())
            .addItemList(list)
            .addItem(PowerMenuItem("All Documents", false))
            .addItem(PowerMenuItem("Excel Files", false))
            .addItem(PowerMenuItem("Word Files", false))
            .addItem(PowerMenuItem("PowerPoint Files", false))
            .addItem(PowerMenuItem("PDF Files", false))
            .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
            .setMenuRadius(10f)
            .setMenuShadow(10f)
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            .setTextGravity(Gravity.CENTER)
            .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
            .setSelectedTextColor(Color.WHITE)
            .setMenuColor(Color.WHITE)
            .setSelectedMenuColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .build()
    }

    private val onMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem> =
        OnMenuItemClickListener<PowerMenuItem> { _, item ->

            when(item.title) {
                "All Documents" -> {
                    sharedViewModel.typeMS.value = ALL
                    binding?.logo?.setImageResource(R.drawable.ic_all)
                }

                "Excel Files" -> {
                    sharedViewModel.typeMS.value = XLS
                    binding?.logo?.setImageResource(R.drawable.ic_excel)
                }

                "Word Files" -> {
                    sharedViewModel.typeMS.value = DOC
                    binding?.logo?.setImageResource(R.drawable.ic_word)
                }

                "PowerPoint Files" -> {
                    sharedViewModel.typeMS.value = PPT
                    binding?.logo?.setImageResource(R.drawable.ic_ppt)
                }

                "PDF Files" -> {
                    sharedViewModel.typeMS.value = PDF
                    binding?.logo?.setImageResource(R.drawable.ic_pdf)
                }
            }


            powerMenu.dismiss()
        }


    private fun setupViewPager() {

        val fragmentList = listOf(
            AllFilesFragment(),
            RecentFragment(),
            FavouriteFragment()
        )

        val fragmentListName = listOf(
            "All Files",
            "Recent",
            "Favourite"
        )

        _viewPagerAdapter = ViewPagerAdapter(
            childFragmentManager,
            lifecycle
        ).also { adapter ->
            adapter.setFragmentList(fragmentList)
        }

        binding?.apply {
            viewPager.adapter = viewPagerAdapter

            _tabLayoutMediator = TabLayoutMediator(
                tabLayout,
                viewPager
            ) { tab, position ->
                tab.text = fragmentListName[position]
            }

            tabLayoutMediator?.attach()

            viewPager.currentItem = 0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.viewPager?.adapter = null

        binding?.viewPager?.removeOnAttachStateChangeListener(null)

        tabLayoutMediator?.detach()

        _tabLayoutMediator = null

        _viewPagerAdapter = null

        _binding = null

    }

}