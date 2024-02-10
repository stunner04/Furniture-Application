package com.example.furnitureapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.furnitureapp.data.Categories
import com.example.furnitureapp.databinding.CategoryRvItemBinding

class CategoriesAdapter :
    RecyclerView.Adapter<CategoriesAdapter.CategoriesRecyclerAdapterViewHolder>() {
    inner class CategoriesRecyclerAdapterViewHolder(val binding: CategoryRvItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Categories>() {
        override fun areItemsTheSame(
            oldItem: Categories,
            newItem: Categories
        ): Boolean {
            return oldItem.rank == newItem.rank
        }

        override fun areContentsTheSame(
            oldItem: Categories,
            newItem: Categories
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoriesAdapter.CategoriesRecyclerAdapterViewHolder {
        return CategoriesRecyclerAdapterViewHolder(
            CategoryRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: CategoriesAdapter.CategoriesRecyclerAdapterViewHolder,
        position: Int
    ) {
        val category = differ.currentList[position]
        holder.binding.apply {
            Glide.with(holder.itemView).load(category.image).into(imgCategory)
            tvCategoryName.text = category.name
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(category)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onItemClick: ((Categories) -> Unit)? = null
}