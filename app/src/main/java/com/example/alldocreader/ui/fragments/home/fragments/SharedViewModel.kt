package com.example.alldocreader.ui.fragments.home.fragments

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alldocreader.MyApplication
import com.example.alldocreader.helper.CheckPermission
import com.example.alldocreader.helper.Constants
import com.example.alldocreader.helper.Constants.ALL
import com.example.alldocreader.helper.EventsHandler
import com.example.alldocreader.helper.PathUtil
import com.example.alldocreader.repository.FilesFetcherRepository
import com.example.alldocreader.repository.RoomDBRepository
import com.example.alldocreader.room.entity.FilesEntity
import com.example.alldocreader.ui.ActivityDocumentViewer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SharedViewModel
@Inject
constructor(
    private val dbRepo: RoomDBRepository,
    private val application: MyApplication,
    private val repo: FilesFetcherRepository,
    private val checkPermission: CheckPermission,
    private val pathUtil: PathUtil,
    ) : ViewModel() {

    private val _event = MutableStateFlow<EventsHandler>(EventsHandler.Idle)
    val event = _event.asStateFlow()

    val typeMS = MutableStateFlow(ALL)
    val type = typeMS.asStateFlow()

    fun checkPermission() {
        viewModelScope.launch {

            val checkPermission1 = checkPermission.checkPermission()

            Timber.d("checkPermission : $checkPermission1")

            _event.emit(
                EventsHandler.Permission(
                    checkPermission1
                )
            )
        }
    }

    private suspend fun addToDatabase(file: FilesEntity) {

        dbRepo.updateInDB(file)

    }

    suspend fun deleteFile(deleted: Boolean, file: FilesEntity) {
        if (deleted) {
            deleteFileFormDB(file)
            Toast.makeText(application, "File deleted successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(application, "Failed to delete", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun deleteFileFormDB(file: FilesEntity) {
        dbRepo.deleteFromDB(file)
    }

    suspend fun setFavourite(file: FilesEntity) {

        addToDatabase(file)

        if (file.favourite == true) {

            file.favourite = false

            updateList(file)

            Toast.makeText(application, "Removed from favourite", Toast.LENGTH_SHORT).show()

        } else {

            file.favourite = true

            updateList(file)

            Toast.makeText(application, "Added to favourite", Toast.LENGTH_SHORT).show()
        }

    }

    suspend fun renameFile(renamed: Boolean, file: FilesEntity, appFile: Boolean) {
        if (renamed) {
            updateList(file)
            Toast.makeText(application, "File renamed successfully", Toast.LENGTH_SHORT).show()
            if (!appFile) {
                repo.updateMediaStore(file)
            }
        } else {
            updateList(file)
            Toast.makeText(application, "Failed to rename", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun updateList(file: FilesEntity) {

        dbRepo.updateInDB(file)

    }

    suspend fun resetValues() {
        _event.emit(EventsHandler.Idle)
    }

    suspend fun openExternalIntent(intent: Intent?) {
        if (intent != null) {


            val path: String?

            val uri = intent.data

            path = uri?.let { it1 -> pathUtil.getPath(it1) }

            Timber.d(path)

            val name = path?.substring(path.lastIndexOf("/") + 1)

            Timber.d(name)

            path?.let {

                val file = File(path)


                val excelEntity = FilesEntity()

                excelEntity.filePath = path

                excelEntity.fileName = name

                uri?.let {

                    val fileSize = (file.length() / 1024).toInt()

                    val formatSize = repo.formatSize(fileSize.toLong())

                    excelEntity.fileSize = formatSize

                }

                val excelIntent = Intent()

                excelIntent.setClass(application, ActivityDocumentViewer::class.java)

                excelIntent.putExtra(Constants.INTENT_FILED_FILE_PATH, excelEntity.filePath)
                excelIntent.putExtra("FileName", excelEntity.fileName)
                excelIntent.putExtra("FileMime", excelEntity.mimeType)

                _event.emit(EventsHandler.MoveForward(excelIntent))

            }

        }
    }


}