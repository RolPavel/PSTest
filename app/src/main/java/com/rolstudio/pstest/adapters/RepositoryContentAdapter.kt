package com.rolstudio.pstest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rolstudio.pstest.R
import com.rolstudio.pstest.models.RepositoryContentItems

class RepositoryContentAdapter(
    private val onItemClick: (RepositoryContentItems) -> Unit
) : RecyclerView.Adapter<RepositoryContentAdapter.ContentViewHolder>() {

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contentIcon: ImageView = itemView.findViewById(R.id.contentIcon)
        var contentName: TextView = itemView.findViewById(R.id.contentName)
        var contentType: TextView = itemView.findViewById(R.id.contentType)
    }

    private val differCallback = object : DiffUtil.ItemCallback<RepositoryContentItems>() {
        override fun areItemsTheSame(
            oldItem: RepositoryContentItems,
            newItem: RepositoryContentItems
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: RepositoryContentItems,
            newItem: RepositoryContentItems
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ContentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_repository_content, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val contentItem = differ.currentList[position]
        holder.contentName.text = contentItem.name
        holder.contentType.text = if (contentItem.type == "file") "Файл" else "Папка"
        val iconResId = if (contentItem.type == "file") {
            R.drawable.ic_file
        } else {
            R.drawable.ic_folder
        }
        holder.contentIcon.setImageResource(iconResId)

        holder.itemView.setOnClickListener {
            onItemClick(contentItem)
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(list: List<RepositoryContentItems>) {
        differ.submitList(list)
    }
}
