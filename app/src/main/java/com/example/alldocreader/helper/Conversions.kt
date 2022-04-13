package com.example.alldocreader.helper

import com.example.alldocreader.room.entity.FilesEntity
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

object Conversions {

    fun stringToList(listString: String): List<String> {
        val gson = GsonBuilder().setPrettyPrinting().create()

        return gson.fromJson(
            listString,
            object : TypeToken<List<String>>() {
            }.type
        )
    }

    fun stringToListPDFEntity(listString: String): List<FilesEntity> {
        val gson = GsonBuilder().setPrettyPrinting().create()

        return gson.fromJson(
            listString,
            object : TypeToken<List<FilesEntity>>() {
            }.type
        )
    }

    fun listToString(pickedImages: List<String>): String? {
        val hGson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        return hGson.toJson(pickedImages)
    }

    fun listToStringPDFEntity(pickedImages: List<FilesEntity>): String? {
        val hGson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        return hGson.toJson(pickedImages)
    }

}