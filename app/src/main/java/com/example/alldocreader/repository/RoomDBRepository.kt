package com.example.alldocreader.repository

import com.example.alldocreader.room.dao.ExcelDao
import com.example.alldocreader.room.entity.FilesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber

class RoomDBRepository(
    private var pdfDao: ExcelDao,
) {

    fun getAllFiles(): Flow<List<FilesEntity>> {
        return pdfDao.getAllFiles()
    }

    fun getSpecificType(type: String): Flow<List<FilesEntity>> {
        return pdfDao.getSpecificType(type)
    }

    suspend fun getDBFiles(): List<FilesEntity> {
        return withContext(Dispatchers.IO) {
             pdfDao.getDBFiles()
        }
    }

    suspend fun insertFile(filesFiles: FilesEntity) {
        withContext(Dispatchers.IO) {
            val insertFile = pdfDao.insertFile(filesFiles)
            Timber.d("File Inserted : $insertFile")
        }
    }

    suspend fun addListToDatabase(fileList: MutableList<FilesEntity>) {
        withContext(Dispatchers.IO) {
            fileList.let {
                fileList.forEach { file ->
                    pdfDao.insertFile(file)
                }
            }
        }
    }

    suspend fun updateInDB(file: FilesEntity) {
        withContext(Dispatchers.IO) {
            val updatedFiles = pdfDao.updateFile(file)
        }
    }

    suspend fun deleteFromDB(file: FilesEntity) {
        withContext(Dispatchers.IO) {
            val deleteFile = pdfDao.deleteFile(file)
            Timber.d("File deleted: $deleteFile")
        }
    }

    suspend fun deleteAllFilesFromDB() {
        withContext(Dispatchers.IO) {
            pdfDao.deleteAllFiles()
        }
    }

    fun getFavouriteFiles(): Flow<List<FilesEntity>> {
        return pdfDao.getFavouriteFiles()
    }

    fun getRecentFiles(): Flow<List<FilesEntity>> {
        return pdfDao.getRecentFiles()
    }

}