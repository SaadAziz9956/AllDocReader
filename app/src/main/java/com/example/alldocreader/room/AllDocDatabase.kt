package com.example.alldocreader.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.alldocreader.room.dao.DocsDao
import com.example.alldocreader.room.entity.FilesEntity


@Database(
    entities = [
        FilesEntity::class
    ], version = 1,
    exportSchema = false
)
abstract class AllDocDatabase : RoomDatabase() {

    abstract fun pdfDao(): DocsDao

    companion object {
        @Volatile
        private var INSTANCE: AllDocDatabase? = null
        private const val DB_NAME = "Excel_Database"

        fun getDbInstance(context: Context): AllDocDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AllDocDatabase::class.java,
                    DB_NAME,
                ).fallbackToDestructiveMigration().build().also {
                    INSTANCE = it
                }
            }
        }
    }

}