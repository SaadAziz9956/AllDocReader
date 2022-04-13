package com.example.alldocreader.helper

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import com.example.alldocreader.databinding.PermissionDialogBinding

object Dialog {

    fun permissionDialog(activity: Activity) {

        val dialogBinding: PermissionDialogBinding = PermissionDialogBinding.inflate(activity.layoutInflater)

        val builder = AlertDialog.Builder(activity)

        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        if (dialog.window != null)

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.apply {

            btnPermission.setOnClickListener {
                RequestPermission.getStoragePermission(activity)
                dialog.dismiss()
            }

        }

        dialog.setCanceledOnTouchOutside(true)

        dialog.show()

    }

}