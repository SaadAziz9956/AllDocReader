package com.example.alldocreader.room.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "excel_table")
data class FilesEntity(

    @PrimaryKey
    var id: Int? = null,
    var fileSize: String? = null,
    var fileId: Long? = null,
    var fileName: String? = null,
    var fileTitle: String? = null,
    var filePath: String? = null,
    var mimeType: String? = null,
    var fileExtension: String? = null,
    var dateCreated: String? = null,
    var favourite: Boolean? = false,
    var recent: Boolean? = false,
    var pageNo: Int? = 0,
    var pageSaved: Boolean? = false
    ): Parcelable