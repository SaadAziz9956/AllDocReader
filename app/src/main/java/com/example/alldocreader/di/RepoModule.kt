package com.example.alldocreader.di

import com.example.alldocreader.MyApplication
import com.example.alldocreader.repository.FilesFetcherRepository
import com.example.alldocreader.repository.RoomDBRepository
import com.example.alldocreader.room.AllDocDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Singleton
    @Provides
    fun provideAllFilesRepository(
        application: MyApplication,
        dbRepo: RoomDBRepository,
    ): FilesFetcherRepository {
        return FilesFetcherRepository(
            application.applicationContext,
            dbRepo
        )
    }

    @Singleton
    @Provides
    fun provideDatabaseRepository(
        allDocDatabase: AllDocDatabase,
    ): RoomDBRepository {
        return RoomDBRepository(
            allDocDatabase.pdfDao()
        )
    }


}