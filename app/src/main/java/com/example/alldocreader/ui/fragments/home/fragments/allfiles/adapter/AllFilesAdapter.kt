package com.example.alldocreader.ui.fragments.home.fragments.allfiles.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alldocreader.R
import com.example.alldocreader.databinding.PdfItemsBinding
import com.example.alldocreader.helper.Constants.DOC
import com.example.alldocreader.helper.Constants.DOCX
import com.example.alldocreader.helper.Constants.PDF
import com.example.alldocreader.helper.Constants.PPT
import com.example.alldocreader.helper.Constants.PPTX
import com.example.alldocreader.helper.Constants.XLS
import com.example.alldocreader.helper.Constants.XLSX
import com.example.alldocreader.room.entity.FilesEntity
import com.example.alldocreader.ui.fragments.home.fragments.allfiles.adapter.viewholder.FileVH

class AllFilesAdapter(
    private val onClick: (file: FilesEntity) -> Unit,
    private val onClickMoreOptions: (file: FilesEntity) -> Unit,
) : RecyclerView.Adapter<FileVH>() {

    private var list: List<FilesEntity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileVH {
        return FileVH(
            PdfItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: FileVH, position: Int) {

        val file = list[position]

        when (file.mimeType) {

            DOC -> {
                holder.binding.ivDocumentType.setImageResource(R.drawable.ic_doc_icon)
            }

            DOCX -> {
                holder.binding.ivDocumentType.setImageResource(R.drawable.ic_doc_icon)
            }

            PPT -> {
                holder.binding.ivDocumentType.setImageResource(R.drawable.ic_ppt_icon)
            }

            PPTX -> {
                holder.binding.ivDocumentType.setImageResource(R.drawable.ic_ppt_icon)
            }

            XLS -> {
                holder.binding.ivDocumentType.setImageResource(R.drawable.excel_icon)
            }

            XLSX -> {
                holder.binding.ivDocumentType.setImageResource(R.drawable.excel_icon)
            }

            PDF -> {
                holder.binding.ivDocumentType.setImageResource(R.drawable.ic_pdf_icon)
            }

        }

        holder.binding.tvDocumentName.text = file.fileName
        holder.binding.tvSize.text = file.fileSize
        holder.binding.tvTime.text = file.dateCreated
        holder.binding.parent.setOnClickListener {
            onClick(file)
        }
        holder.binding.ivMore.setOnClickListener {
            onClickMoreOptions(file)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<FilesEntity>) {
        this.list = list
        notifyDataSetChanged()
    }

}