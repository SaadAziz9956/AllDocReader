package com.example.alldocreader.ui.fragments.search.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alldocreader.databinding.PdfSearchItemBinding
import com.example.alldocreader.ui.fragments.search.adapter.viewholder.SearchAdapterVH
import com.example.alldocreader.room.entity.FilesEntity

class SearchAdapter(
    private val onClick: (file: FilesEntity) -> Unit,
) : RecyclerView.Adapter<SearchAdapterVH>() {

    private var list: List<FilesEntity> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapterVH {
        return SearchAdapterVH(
            PdfSearchItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: SearchAdapterVH, position: Int) {

        val file = list[position]

        holder.binding.tvDocumentName.text = file.fileName
        holder.binding.tvSize.text = file.fileSize
        holder.binding.tvTime.text = file.dateCreated
        holder.binding.parent.setOnClickListener {
            onClick(file)
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