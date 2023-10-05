package com.example.furnitureapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.furnitureapp.databinding.SizeRvItemBinding

class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizesViewHolder>() { // 1

    private var selectedPosition = -1

    inner class SizesViewHolder(private val binding: SizeRvItemBinding) :
        ViewHolder(binding.root) // 2
    { // 6
        fun bind(size: String, positon: Int) {
            binding.tvSize.text = size
            if (positon == selectedPosition) // size is selected
            {
                binding.apply {
                    imageColor.visibility = View.VISIBLE
                }
            } else // // size is not selected
            {
                binding.apply {
                    imageColor.visibility = View.INVISIBLE
                }
            }
        }
    }

    val differCallback = object : DiffUtil.ItemCallback<String>() { // 3
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback) // 4

    //5
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder {
        return SizesViewHolder(SizeRvItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size, position)
        //7
        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0) {
                notifyItemChanged(selectedPosition) //unselect call to bind
            }
            selectedPosition = holder.adapterPosition // select call to bind
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(size)
        }
    }

    var onItemClick: ((String) -> Unit)? = null
}