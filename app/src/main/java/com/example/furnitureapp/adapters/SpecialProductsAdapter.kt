package com.example.furnitureapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.furnitureapp.data.Product
import com.example.furnitureapp.databinding.SpecialRvItemBinding

class SpecialProductsAdapter : RecyclerView.Adapter<SpecialProductsAdapter.SpecialProductsViewHolder>() { //1

    inner class SpecialProductsViewHolder(private val binding: SpecialRvItemBinding)  : RecyclerView.ViewHolder(binding.root) //2
    {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images).into(imgSpecialRV)
                tvSpecialName.text=product.name
                tvSpecialAdPrice.text=product.price.toString()
            }
        }

        /* GLIDE IMAGE LOADING ERROR trial FIX, LOGCAT-> Due to NOT supported datatype of Product Image URL i.e, List<>
        fun bind(product: Product) {
            val imageUrls = ArrayList<String>()
            binding.apply {

                for (imageUrl in imageUrls) {
                    Glide.with(itemView)
                        .load(imageUrl)
                        .into(imgSpecialRV) // Load each image individually
                }

                tvSpecialName.text=product.name
                tvSpecialAdPrice.text=product.price.toString()
            }
        }
         */
    }

    private val diffCallback = object: DiffUtil.ItemCallback<Product>(){ // 3

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean { // 3.5
            return newItem.id==oldItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean { // 3.5
            return newItem==oldItem
        }

    }

    val differ = AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductsViewHolder {   // 1.5
        return SpecialProductsViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int { // 1.5
     return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SpecialProductsViewHolder, position: Int) { // 1.5
        val product = differ.currentList[position]
        holder.bind(product)
    }
}