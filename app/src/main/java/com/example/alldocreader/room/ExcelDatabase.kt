package com.example.alldocreader.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.alldocreader.room.dao.ExcelDao
import com.example.alldocreader.room.entity.FilesEntity


@Database(
    entities = [
        FilesEntity::class
    ], version = 1,
    exportSchema = false
)
abstract class ExcelDatabase : RoomDatabase() {

    abstract fun pdfDao(): ExcelDao

    companion object {
        @Volatile
        private var INSTANCE: ExcelDatabase? = null
        private const val DB_NAME = "Excel_Database"

        fun getDbInstance(context: Context): ExcelDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ExcelDatabase::class.java,
                    DB_NAME,
                ).fallbackToDestructiveMigration().build().also {
                    INSTANCE = it
                }
            }
        }
    }

}