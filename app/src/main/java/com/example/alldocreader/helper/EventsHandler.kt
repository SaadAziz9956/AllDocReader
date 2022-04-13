package com.example.alldocreader.helper

import android.content.Intent
import com.example.alldocreader.room.entity.FilesEntity
import kotlinx.coroutines.flow.Flow

sealed class EventsHandler {

    object Idle : EventsHandler()

    data class MoveForward(
        val intent: Intent? = null
    ) : EventsHandler()

    data class Permission(
        val permission: Boolean? = null
    ) : EventsHandler()

    data class Files(
        val files: Flow<List<FilesEntity>>? = null
    ) : EventsHandler()

}