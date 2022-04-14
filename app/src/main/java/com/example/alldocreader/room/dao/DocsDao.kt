package com.example.alldocreader.room.dao

import androidx.room.*
import com.example.alldocreader.room.entity.FilesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFile(file: FilesEntity): Long?

    @Query("SELECT * FROM excel_table")
    fun getAllFiles(): Flow<List<FilesEntity>>

    @Query("SELECT * FROM excel_table")
    fun getDBFiles(): List<FilesEntity>

    @Update
    suspend fun updateFile(file: FilesEntity): Int

    @Delete
    suspend fun deleteFile(file: FilesEntity): Int

    @Query("DELETE FROM excel_table")
    suspend fun deleteAllFiles(): Int

    @Query("SELECT * FROM excel_table WHERE favourite = 1")
    fun getFavouriteFiles(): Flow<List<FilesEntity>>

    @Query("SELECT * FROM excel_table WHERE recent = 1")
    fun getRecentFiles(): Flow<List<FilesEntity>>

    @Query("SELECT * FROM excel_table WHERE mimeType = :type")
    fun getSpecificType(type: String): Flow<List<FilesEntity>>

}