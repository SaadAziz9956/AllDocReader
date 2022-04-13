package com.example.alldocreader.ui.fragments.home.fragments.favourite.viewmodel

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alldocreader.MyApplication
import com.example.alldocreader.helper.Constants
import com.example.alldocreader.helper.EventsHandler
import com.example.alldocreader.repository.RoomDBRepository
import com.example.alldocreader.room.entity.FilesEntity
import com.example.alldocreader.ui.ActivityDocumentViewer
import com.example.alldocreader.ui.pdfviewer.PdfViewer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel
@Inject
constructor(
    private var application: MyApplication,
    private var repo: RoomDBRepository
) : ViewModel() {

    private val _event = MutableStateFlow<EventsHandler>(EventsHandler.Idle)
    val event = _event.asStateFlow()

    fun getFavouriteFiles() = repo.getFavouriteFiles()
        .map { list ->
            list.sortedByDescending { item ->
                item.fileSize
            }
        }

    fun sendIntentRequest(filesFile: FilesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            intentRequest(filesFile)
        }
    }

    private suspend fun intentRequest(file: FilesEntity) {

        if (file.mimeType == Constants.PDF) {
            Intent(application, PdfViewer::class.java).also { intent ->
                Bundle().let { bundle ->
                    bundle.putParcelable(Constants.FILE, file)
                    intent.putExtras(bundle)
                }
                _event.emit(EventsHandler.MoveForward(intent))
            }
        } else {
            Intent(application, ActivityDocumentViewer::class.java).also { intent ->
                intent.putExtra(Constants.INTENT_FILED_FILE_PATH, file.filePath)
                intent.putExtra("FileName", file.fileName)
                intent.putExtra("FileMime", file.mimeType)
                _event.emit(EventsHandler.MoveForward(intent))
            }
        }

    }

    suspend fun resetValues() {
        _event.emit(EventsHandler.Idle)
    }

}