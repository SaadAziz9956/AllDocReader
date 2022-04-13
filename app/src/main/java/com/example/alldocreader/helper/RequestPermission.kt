package com.example.alldocreader.helper

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat.requestPermissions
import com.example.alldocreader.helper.Constants.REQUEST_WRITE_PERMISSION

object RequestPermission {

    fun getStoragePermission(activity: Activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            val intent = Intent()

            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION

            val uri = Uri.fromParts("package", activity.packageName, null)

            intent.data = uri

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            activity.startActivity(intent)

        } else {

            requestPermissions(activity,

                arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),

                REQUEST_WRITE_PERMISSION

            )

        }

    }

}