package com.example.alldocreader.ui.fragments.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alldocreader.MyApplication
import com.example.alldocreader.helper.EventsHandler
import com.example.alldocreader.repository.RoomDBRepository
import com.example.alldocreader.room.entity.FilesEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val dbRepo: RoomDBRepository,
    private val application: MyApplication,
) : ViewModel() {

    private val _event = MutableStateFlow<EventsHandler>(EventsHandler.Idle)
    val event = _event.asStateFlow()

    fun getFileFromDB() = dbRepo.getAllFiles()

    fun sendIntentRequest(filesFile: FilesEntity) {

        viewModelScope.launch {

            intentRequest(filesFile)

        }

    }

    private suspend fun intentRequest(file: FilesEntity) {
//
//        Intent(application, PdfViewer::class.java).also { intent ->
//
//            Bundle().let { bundle ->
//                bundle.putParcelable(FILE, file)
//                intent.putExtras(bundle)
//            }
//
//            _event.emit(EventsHandler.MoveForward(intent))
//
//        }

        file.recent = true

        updateDB(file)

    }


    private suspend fun updateDB(file: FilesEntity) {

        dbRepo.updateInDB(file)

    }


    fun filterList(charSequence: CharSequence, list: MutableList<FilesEntity>): MutableList<FilesEntity> {
        val charString = charSequence.toString()
        val tempList = mutableListOf<FilesEntity>()
        for (row in list) {
            if (row.fileName?.lowercase(Locale.getDefault())!!.contains(
                    charString.lowercase(
                        Locale.getDefault()
                    )
                )
            ) {
                tempList.add(row)
            }
        }
        return tempList
    }

    suspend fun resetValues() {
        _event.emit(EventsHandler.Idle)
    }
}