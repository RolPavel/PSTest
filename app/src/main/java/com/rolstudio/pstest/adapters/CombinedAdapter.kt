package com.rolstudio.pstest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rolstudio.pstest.R
import com.rolstudio.pstest.util.CombinedItem

class CombinedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userImage: ImageView = itemView.findViewById(R.id.contentIcon)
        var userName: TextView = itemView.findViewById(R.id.contentName)
        var userScore: TextView = itemView.findViewById(R.id.contentType)
    }

    inner class RepositoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var repoName: TextView = itemView.findViewById(R.id.repName)
        var repoDescription: TextView = itemView.findViewById(R.id.repDescription)
        var repoCountBranches: TextView = itemView.findViewById(R.id.repCountBranches)
    }

    private val differCallback = object : DiffUtil.ItemCallback<CombinedItem>() {
        override fun areItemsTheSame(oldItem: CombinedItem, newItem: CombinedItem): Boolean {
            return when {
                oldItem is CombinedItem.User && newItem is CombinedItem.User -> oldItem.item.login == newItem.item.login
                oldItem is CombinedItem.Repository && newItem is CombinedItem.Repository -> oldItem.item.name == newItem.item.name
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: CombinedItem, newItem: CombinedItem): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    override fun getItemViewType(position: Int): Int {
        return when (differ.currentList[position]) {
            is CombinedItem.User -> 1
            is CombinedItem.Repository -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> UserViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
            )
            else -> RepositoryViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_replist, parent, false)
            )
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    private var onItemClickListener: ((CombinedItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (CombinedItem) -> Unit) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> {
                val userItem = differ.currentList[position] as CombinedItem.User
                val user = userItem.item
                Glide.with(holder.userImage)
                    .load(user.avatar_url)
                    .into(holder.userImage)
                holder.userName.text = user.login
                holder.userScore.text = user.score.toString()
                holder.itemView.setOnClickListener {
                    onItemClickListener?.let { click ->
                        click(userItem)
                    }
                }
            }
            is RepositoryViewHolder -> {
                val repoItem = differ.currentList[position] as CombinedItem.Repository
                val repo = repoItem.item
                holder.repoName.text = repo.name
                holder.repoDescription.text = repo.description
                    ?: "No Description" // Обработайте возможность отсутствия описания
                holder.repoCountBranches.text = repo.forks_count.toString()
                holder.itemView.setOnClickListener {
                    onItemClickListener?.let { click ->
                        click(repoItem)
                    }
                }
            }
        }
    }
    fun submitList(list: List<CombinedItem>) {
        differ.submitList(list)
    }
}
