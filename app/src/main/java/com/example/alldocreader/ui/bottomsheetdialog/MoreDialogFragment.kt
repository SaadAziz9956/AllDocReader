package com.example.alldocreader.ui.bottomsheetdialog

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.example.alldocreader.R
import com.example.alldocreader.databinding.DeleteDialogBinding
import com.example.alldocreader.databinding.FragmentMoreDialogListDialogBinding
import com.example.alldocreader.databinding.RenameDialogBinding
import com.example.alldocreader.helper.Constants
import com.example.alldocreader.helper.Constants.FILE_NAME
import com.example.alldocreader.helper.Constants.FILE_PATH
import com.example.alldocreader.helper.Constants.FILE_SIZE
import com.example.alldocreader.room.entity.FilesEntity
import com.example.alldocreader.ui.DetailsActivity
import com.example.alldocreader.ui.fragments.home.fragments.SharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber
import java.io.File

class MoreDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentMoreDialogListDialogBinding? = null
    private val binding get() = _binding

    private val shareViewModel by activityViewModels<SharedViewModel>()

    private var activity: Boolean? = false

    private var file = FilesEntity()

    private lateinit var callBack: CallBack

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentMoreDialogListDialogBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        onClick()
    }

    private fun initViews() {

        when (file.mimeType) {

            Constants.DOC -> {
                binding?.ivIcon?.setImageResource(R.drawable.ic_doc_icon)
                binding?.tvType?.text = getString(R.string.word)
            }

            Constants.DOCX -> {
                binding?.ivIcon?.setImageResource(R.drawable.ic_doc_icon)
                binding?.tvType?.text = getString(R.string.word)
            }

            Constants.PPT -> {
                binding?.ivIcon?.setImageResource(R.drawable.ic_ppt_icon)
                binding?.tvType?.text = getString(R.string.ppt)
            }

            Constants.PPTX -> {
                binding?.ivIcon?.setImageResource(R.drawable.ic_ppt_icon)
                binding?.tvType?.text = getString(R.string.ppt)
            }

            Constants.XLS -> {
                binding?.ivIcon?.setImageResource(R.drawable.excel_icon)
                binding?.tvType?.text = getString(R.string.excel)
            }

            Constants.XLSX -> {
                binding?.ivIcon?.setImageResource(R.drawable.excel_icon)
                binding?.tvType?.text = getString(R.string.excel)
            }

            Constants.PDF -> {
                binding?.ivIcon?.setImageResource(R.drawable.ic_pdf_icon)
                binding?.tvType?.text = getString(R.string.pdf)
            }

        }
        
        
        binding?.apply {
            fileName.text = file.fileName
            tvSize.text = file.fileSize
        }
    }

    private fun onClick() {

        binding?.rename?.setOnClickListener {
            renameDialog()
        }

        binding?.delete?.setOnClickListener {
            deleteDialog()
        }

        binding?.favourite?.setOnClickListener {
            addToFavourite()
        }

        binding?.share?.setOnClickListener {
            shareFile()
        }

        binding?.details?.setOnClickListener {
            detailsIntent()
        }

    }

    private fun shareFile() {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        val imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            File(file.filePath)
        )
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intentShareFile.type = file.mimeType
        intentShareFile.putExtra(Intent.EXTRA_STREAM, imageUri)
        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...")
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...")
        startActivity(Intent.createChooser(intentShareFile, "Share File"))
    }

    private fun detailsIntent() {
        Intent(requireContext(), DetailsActivity::class.java).also { intent ->
            Bundle().let { bundle ->
                bundle.putString(FILE_NAME, file.fileName)
                bundle.putString(FILE_PATH, file.filePath)
                bundle.putString(FILE_SIZE, file.fileSize)
                bundle.putString("mime", file.mimeType)
                intent.putExtras(bundle)
            }

            val options =
                ActivityOptions.makeCustomAnimation(
                    context,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
            startActivity(intent, options.toBundle())
        }
    }

    private fun addToFavourite() {
        dismiss()
        callBack.isFavourite(file)
    }

    private fun deleteDialog() {
        val dialogBinding: DeleteDialogBinding = DeleteDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        if (dialog.window != null)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.apply {
            ok.setOnClickListener {

                val selectedFile = File(file.filePath!!)

                if (selectedFile.exists()) {
                    val deleted = selectedFile.delete()
                    if (deleted) {
                        deleteCREntryForFilePath(requireContext(), file.filePath, file.fileId)
                        callBack.deleteFile(deleted, file)
                        dialog.dismiss()
                        dismiss()
                    } else {
                        callBack.deleteFile(false, file)
                        dialog.dismiss()
                        dismiss()
                    }
                } else {
                    deleteCREntryForFilePath(requireContext(), file.filePath, file.fileId)
                    callBack.deleteFile(true, file)
                    dialog.dismiss()
                    dismiss()
                }

            }

            cancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun deleteCREntryForFilePath(
        context: Context,
        filePath: String?,
        fileID: Long?
    ): Boolean {
        var fDeleted = false
        val rowsDeleted: Int
        val imageURI = MediaStore.Files.getContentUri("external")
        val deleteStr = MediaStore.Files.FileColumns._ID + "=" + fileID
        MediaScannerConnection.scanFile(context, arrayOf(filePath), null, null)

        rowsDeleted = context.contentResolver.delete(
            imageURI, deleteStr, null
        )
        if (rowsDeleted > 0) fDeleted = true
        return fDeleted
    }

    private fun renameDialog() {
        val dialogBinding: RenameDialogBinding = RenameDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        if (dialog.window != null)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBinding.apply {
            etRename.setText(file.fileName)
            ok.setOnClickListener {

                var newName = etRename.editableText.toString()
                Timber.d("name : $newName")

                var newTitle: String? = null

                when (file.mimeType) {

                    Constants.DOC, Constants.DOCX -> {
                        if (!newName.endsWith(".doc") || !newName.endsWith(".docx")) {

                            newTitle = newName

                            if (file.fileName?.endsWith(".doc") == true) {
                                newName = "$newName.doc"
                            } else if (file.fileName?.endsWith(".docx") == true) {
                                newName = "$newName.docx"
                            }

                        } else {
                            if (newName.contains(".doc")) {

                                newName.replace(".doc", "")

                                newTitle = newName

                            } else if (newName.contains(".docx")) {

                                newName.replace(".docx", "")

                                newTitle = newName

                            }

                        }
                    }

                    Constants.PPT, Constants.PPTX -> {
                        if (!newName.endsWith(".ppt") || !newName.endsWith(".pptx")) {

                            newTitle = newName

                            if (file.fileName?.endsWith(".ppt") == true) {
                                newName = "$newName.ppt"
                            } else if (file.fileName?.endsWith(".pptx") == true) {
                                newName = "$newName.pptx"
                            }

                        } else {
                            if (newName.contains(".ppt")) {

                                newName.replace(".ppt", "")

                                newTitle = newName

                            } else if (newName.contains(".pptx")) {

                                newName.replace(".pptx", "")

                                newTitle = newName

                            }

                        }
                    }

                    Constants.XLS, Constants.XLSX-> {
                        if (!newName.endsWith(".xls") || !newName.endsWith(".xlsx")) {

                            newTitle = newName

                            if (file.fileName?.endsWith(".xls") == true) {
                                newName = "$newName.xls"
                            } else if (file.fileName?.endsWith(".xlsx") == true) {
                                newName = "$newName.xlsx"
                            }

                        } else {
                            if (newName.contains(".xls")) {

                                newName.replace(".xls", "")

                                newTitle = newName

                            } else if (newName.contains(".xlsx")) {

                                newName.replace(".xlsx", "")

                                newTitle = newName

                            }

                        }
                    }

                    Constants.PDF -> {
                        if (!newName.endsWith(".pdf")) {

                            newTitle = newName

                            if (file.fileName?.endsWith(".pdf") == true) {
                                newName = "$newName.pdf"
                            }

                        } else {
                            if (newName.contains(".pdf")) {

                                newName.replace(".pdf", "")

                                newTitle = newName

                            }
                        }
                    }

                }

                val fileRenamed = newTitle?.let { it1 -> rename(newName, it1) }

                if (fileRenamed == true) {
                    dialog.dismiss()
                    dismiss()
                    callBack.renameFile(fileRenamed, file)
                }

            }

            cancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun rename(newName: String, newTitle: String): Boolean {

        var fileRenamed = false
        var path = file.filePath

        val mFile = File(path!!).parent

        val from = File("$mFile/", file.fileName!!)
        val to = File("$mFile/", newName)

        if (from == to) {
            Toast.makeText(
                requireContext(),
                "File name already exist",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            val renamed = from.renameTo(to)

            if (renamed) {
                path = path.substring(0, path.lastIndexOf("/"))
                file.fileName = newName
                file.fileTitle = newTitle
                file.filePath = "$path/$newName"
                fileRenamed = true
            } else {
                fileRenamed = false
            }

        }

        return fileRenamed

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setData(filesFile: FilesEntity, activity: Boolean) {
        file = filesFile
        this.activity = activity
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        when (activity) {

            true -> {
                callBack = context as CallBack
            }

            false -> {
                callBack = parentFragment as CallBack
            }

            else -> Unit
        }

    }

    interface CallBack {

        fun renameFile(renamed: Boolean, file: FilesEntity)

        fun deleteFile(deleted: Boolean, file: FilesEntity)

        fun isFavourite(file: FilesEntity)

    }

}