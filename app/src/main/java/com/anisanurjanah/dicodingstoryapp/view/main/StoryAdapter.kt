package com.anisanurjanah.dicodingstoryapp.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anisanurjanah.dicodingstoryapp.data.remote.response.StoryItem
import com.anisanurjanah.dicodingstoryapp.databinding.ItemStoryBinding
import com.anisanurjanah.dicodingstoryapp.utils.withDateFormat
import com.bumptech.glide.Glide

class StoryAdapter : PagingDataAdapter<StoryItem, StoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ListViewHolder(private var binding: ItemStoryBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoryItem?) {
            binding.apply {
                storyName.text = item?.name
                storyDescription.text = item?.description
                storyDate.text = item?.createdAt?.withDateFormat()
                Glide.with(itemView.context)
                    .load(item?.photoUrl)
                    .into(storyImage)

                itemView.setOnClickListener {
                    onItemClickCallback.onItemClicked(item)
                }
            }
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(items: StoryItem?)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItem>() {
            override fun areItemsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryItem, newItem: StoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}