package com.example.alldocreader.di

import android.content.Context
import com.example.alldocreader.MyApplication
import com.example.alldocreader.helper.CheckPermission
import com.example.alldocreader.helper.PathUtil
import com.example.alldocreader.room.AllDocDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesApplication(
        @ApplicationContext app: Context
    ): MyApplication {
        return app as MyApplication
    }

    @Singleton
    @Provides
    fun providesRoomDb(
        @ApplicationContext context: Context
    ): AllDocDatabase {
        return AllDocDatabase.getDbInstance(context)
    }

    @Singleton
    @Provides
    fun providesPermissionUtils(
        @ApplicationContext context: Context
    ): CheckPermission {
        return CheckPermission(context)
    }

    @Singleton
    @Provides
    fun providesPathUtils(
        @ApplicationContext context: Context
    ): PathUtil {
        return PathUtil(context)
    }
}