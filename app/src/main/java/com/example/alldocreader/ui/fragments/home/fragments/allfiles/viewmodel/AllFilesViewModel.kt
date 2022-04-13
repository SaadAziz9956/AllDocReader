package com.example.alldocreader.ui.fragments.home.fragments.allfiles.viewmodel

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alldocreader.MyApplication
import com.example.alldocreader.helper.Constants
import com.example.alldocreader.helper.Constants.ALL
import com.example.alldocreader.helper.Constants.INTENT_FILED_FILE_PATH
import com.example.alldocreader.helper.Constants.PDF
import com.example.alldocreader.helper.EventsHandler
import com.example.alldocreader.repository.FilesFetcherRepository
import com.example.alldocreader.repository.RoomDBRepository
import com.example.alldocreader.room.entity.FilesEntity
import com.example.alldocreader.ui.ActivityDocumentViewer
import com.example.alldocreader.ui.pdfviewer.PdfViewer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AllFilesViewModel
@Inject
constructor(
    private val repo: FilesFetcherRepository,
    private val dbRepo: RoomDBRepository,
    private val application: MyApplication,
) : ViewModel() {

    private var job: Job? = null


    private val _event = MutableStateFlow<EventsHandler>(EventsHandler.Idle)
    val event = _event.asStateFlow()

    fun sendFileRequest() {
        viewModelScope.launch(Dispatchers.IO) {

            Timber.d("sendFileRequest")

            repo.getFilesFromStorage()

        }
    }

    suspend fun getFileFromDb(type: String) {

        job?.cancel()
        job = viewModelScope.launch {
            delay(500L)
            when (type) {
                ALL -> {
                    Timber.d("ALL")
                    _event.emit(
                        EventsHandler.Files(
                            dbRepo.getAllFiles()
                                .map { list ->
                                    list.sortedByDescending { file ->
                                        file.dateCreated?.lowercase(Locale.getDefault())
                                    }
                                })
                    )
                }
                else -> {
                    Timber.d("ELSE")
                    _event.emit(
                        EventsHandler.Files(
                            dbRepo.getSpecificType(type)
                                .map { list ->
                                    list.sortedByDescending { file ->
                                        file.dateCreated?.lowercase(Locale.getDefault())
                                    }
                                })
                    )
                }
            }
        }
    }

    fun sendIntentRequest(file: FilesEntity) {
        viewModelScope.launch {
            intentRequest(file)
        }
    }

    private suspend fun intentRequest(file: FilesEntity) {

        file.recent = true
        updateDB(file)

        if (file.mimeType == PDF) {
            Intent(application, PdfViewer::class.java).also { intent ->
                Bundle().let { bundle ->
                    bundle.putParcelable(Constants.FILE, file)
                    intent.putExtras(bundle)
                }
                _event.emit(EventsHandler.MoveForward(intent))
            }
        } else {
            Intent(application, ActivityDocumentViewer::class.java).also { intent ->
                intent.putExtra(INTENT_FILED_FILE_PATH, file.filePath)
                intent.putExtra("FileName", file.fileName)
                intent.putExtra("FileMime", file.mimeType)
                _event.emit(EventsHandler.MoveForward(intent))
            }
        }

    }

    private suspend fun updateDB(file: FilesEntity) {

        dbRepo.updateInDB(file)

    }

    suspend fun resetValues() {
        _event.emit(EventsHandler.Idle)
    }

}