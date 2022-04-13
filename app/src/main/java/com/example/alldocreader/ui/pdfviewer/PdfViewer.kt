package com.example.alldocreader.ui.pdfviewer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.example.alldocreader.R
import com.example.alldocreader.databinding.ActivityPdfViewerBinding
import com.example.alldocreader.helper.Constants.FILE
import com.example.alldocreader.room.entity.FilesEntity
import com.example.alldocreader.ui.pdfviewer.viewmodel.PdfViewerViewModel
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File


@AndroidEntryPoint
class PdfViewer : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewerBinding
    private var show = true
    private lateinit var pdfFile: File
    private var horizontal: Boolean = false
    private var darkMode: Boolean = false
    private lateinit var file: FilesEntity
    private val viewModel by viewModels<PdfViewerViewModel>()
    private var defaultPage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        statusBarColor()

        getExtraIntent()

        onClick()

    }

    @Suppress("DEPRECATION")
    private fun statusBarColor() {
        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.light_grey)
    }

    private fun getExtraIntent() {
        file = intent.getParcelableExtra(FILE)!!
        binding.tvFileName.text = file.fileName
        getFilePath(file)
    }

    private fun getFilePath(file: FilesEntity) {

        val filePath = file.filePath
        val pageNo = file.pageNo
        defaultPage = if (pageNo!! > 0) {
            pageNo
        } else {
            0
        }

        if (filePath != null) {
            Timber.d("File Path : $filePath")
            pdfFile = File(filePath)
            openPdfFile(
                horizontal, darkMode,
                pageSnap = false,
                autoSpacing = false,
                pageFling = false,
                defaultPage
            )
        }

    }

    private fun openPdfFile(
        horizontal: Boolean,
        darkMode: Boolean,
        pageSnap: Boolean,
        autoSpacing: Boolean,
        pageFling: Boolean,
        pageNo: Int
    ) {
        binding.pdfView.fromFile(pdfFile)
            .spacing(5)
            .defaultPage(pageNo)
            .scrollHandle(DefaultScrollHandle(this))
            .nightMode(darkMode)
            .swipeHorizontal(horizontal)
            .pageSnap(pageSnap)
            .autoSpacing(autoSpacing)
            .pageFling(pageFling)
            .load()
    }

    private fun onClick() {

        binding.apply {

            ivBack.setOnClickListener {

                onBackPressed()

            }

        }

    }

    private fun setDarkMode() {
        when (darkMode) {

            true -> {

                darkMode = false

                binding.pdfView.setNightMode(false)
                binding.pdfView.loadPages()

            }

            false -> {

                darkMode = true

                binding.pdfView.setNightMode(true)
                binding.pdfView.loadPages()

            }

        }
    }

    private fun setOrientation(darkMode: Boolean) {
        when (horizontal) {

            true -> {

                horizontal = false

                openPdfFile(
                    horizontal, darkMode,
                    pageSnap = false,
                    autoSpacing = false,
                    pageFling = false,
                    defaultPage
                )

            }

            false -> {

                horizontal = true

                openPdfFile(
                    horizontal, darkMode,
                    pageSnap = true,
                    autoSpacing = true,
                    pageFling = true,
                    defaultPage
                )

            }

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        savePageNo()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun savePageNo() {
        val currentPage = binding.pdfView.currentPage
        viewModel.updateDatabase(file, currentPage)
    }
}