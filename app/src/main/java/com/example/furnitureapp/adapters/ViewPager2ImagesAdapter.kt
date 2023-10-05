package com.example.furnitureapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.furnitureapp.databinding.ViewpageImageItemBinding

class ViewPager2ImagesAdapter :
    RecyclerView.Adapter<ViewPager2ImagesAdapter.ViewPager2ImagesViewHolder>() { // 1

    inner class ViewPager2ImagesViewHolder(val binding: ViewpageImageItemBinding) :
        ViewHolder(binding.root) // 2
    {
        fun bind(imagePath: String) { //6
            Glide.with(itemView).load(imagePath).into(binding.imageProductDetails)
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() { // 3
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback) // 4

    //5
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2ImagesViewHolder {
        return ViewPager2ImagesViewHolder(
            ViewpageImageItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewPager2ImagesViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }

}