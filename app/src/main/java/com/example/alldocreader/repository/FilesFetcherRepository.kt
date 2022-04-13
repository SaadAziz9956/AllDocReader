package com.example.alldocreader.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.alldocreader.room.entity.FilesEntity
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FilesFetcherRepository(
    private var context: Context,
    private var dbRepo: RoomDBRepository
) {

    suspend fun getFilesFromStorage(): MutableList<FilesEntity> {

        val fileList = mutableListOf<FilesEntity>()

        val extensions = mutableListOf<String>()
        val mimes = mutableListOf<String>()

        var count = 0

        val contentResolver: ContentResolver = context.contentResolver

        val uri = MediaStore.Files.getContentUri("external")

        val projection = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.SIZE,
        )

        extensions.add("xls")
        extensions.add("xlsx")
        extensions.add("ppt")
        extensions.add("pptx")
        extensions.add("doc")
        extensions.add("docx")
        extensions.add("pdf")

        for (ext in extensions) {
            mimes.add(MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)!!)
        }

        val cursor: Cursor? = contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )

        if (cursor != null) {
            val mimeColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)
            fileList.clear()
            while (cursor.moveToNext()) {
                val mimeType = cursor.getString(mimeColumnIndex)

                if (mimes.contains(mimeType)) {
                    val makeFile = makeFile(cursor, count)

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        val path = makeFile.filePath
                        val filename: String? = path?.substring(path.lastIndexOf("/") + 1)
                        makeFile.fileName = filename
                    }

                    Timber.d("File name: ${makeFile.fileName}")
                    Timber.d("File ID: ${makeFile.fileId}")

                    count += 1

                    fileList.add(makeFile)
                }

            }

            cursor.close()
        }

        addToDatabase(fileList)

        return fileList
    }

    @SuppressLint("SimpleDateFormat")
    private fun makeFile(cursor: Cursor, count: Int): FilesEntity {

        val result = FilesEntity()

        val idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)

        val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

        val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)

        val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)

        val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

        val pathColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)

        val id = cursor.getLong(idCol)

        val mimeType = cursor.getString(mimeCol)

        val fileName = cursor.getString(nameCol)

        val fileTitle = cursor.getString(titleCol)

        val fileSize = cursor.getInt(sizeCol)

        val filePath = cursor.getString(pathColumnIndex)

        result.fileId = id
        result.fileSize = formatSize(fileSize.toLong())
        result.id = count
        result.fileName = fileName
        result.fileTitle = fileTitle
        result.filePath = filePath
        result.mimeType = mimeType
        Timber.d("MIme type : $mimeType")
        result.fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

        val dateFormat = SimpleDateFormat("h:mm a dd MMM yy")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val file = File(filePath)
        val lastModDate = Date(file.lastModified())
        result.dateCreated = dateFormat.format(lastModDate)

        return result
    }

    fun formatSize(fileSize: Long): String {
        var size = fileSize
        var suffix: String? = null
        if (size >= 1024) {
            suffix = " KB"
            size /= 1024
            if (size >= 1024) {
                suffix = " MB"
                size /= 1024
            }
        }
        val resultBuffer = StringBuilder(size.toString())
        var commaOffset = resultBuffer.length - 3
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',')
            commaOffset -= 3
        }
        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }

    private suspend fun addToDatabase(fileList: MutableList<FilesEntity>) {

        val dbFiles = dbRepo.getDBFiles()

        dbFiles.forEach { dbFile ->

            fileList.forEach { storageFile ->

                if ( dbFile.fileId == storageFile.fileId ) {

                    dbFile.fileName = storageFile.fileName
                    dbFile.fileTitle = storageFile.fileTitle
                    dbFile.filePath = storageFile.filePath

                    dbRepo.updateInDB(dbFile)

                }

            }

        }

        Timber.d("addToDatabase")

        dbRepo.addListToDatabase(fileList)

    }

    fun updateMediaStore(file: FilesEntity) {

        val values = ContentValues()

        values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, file.fileName)
        values.put(MediaStore.Files.FileColumns.TITLE, file.fileTitle)
        values.put(MediaStore.Files.FileColumns.DATA, file.filePath)

        val uri = file.fileId?.let {
            ContentUris.withAppendedId(
                MediaStore.Files.getContentUri("external"),
                it
            )
        }

        val res = uri?.let { context.contentResolver.update(it, values, null, null) }

        Timber.d("Result : $res")

    }

}