package com.example.alldocreader.ui.pdfviewer.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alldocreader.MyApplication
import com.example.alldocreader.repository.RoomDBRepository
import com.example.alldocreader.room.entity.FilesEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PdfViewerViewModel
@Inject
constructor(
    private val dbRepo: RoomDBRepository,
    private val context: MyApplication
) : ViewModel() {


    fun updateDatabase(file: FilesEntity, currentPage: Int) {
        file.pageSaved = true
        file.pageNo = currentPage
        Timber.d("PdfViewer")
        Timber.d("recent ${file.recent}")
        Timber.d("favourite ${file.favourite}")
        updateDB(file)
    }

    fun addToFav(file: FilesEntity) {

        when(file.favourite){

            true -> {

                Toast.makeText(
                    context,
                    "Removed from favourite",
                    Toast.LENGTH_SHORT
                ).show()

                file.favourite = false

                updateDB(file)

            }

            false -> {

                Toast.makeText(
                    context,
                    "Added to favourite",
                    Toast.LENGTH_SHORT
                ).show()

                file.favourite = true

                updateDB(file)

            }
            else -> Unit
        }

    }

    private fun updateDB(file: FilesEntity) {
        viewModelScope.launch {
            dbRepo.updateInDB(file)
        }
    }

}