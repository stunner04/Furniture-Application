package com.example.furnitureapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.furnitureapp.data.Product
import com.example.furnitureapp.databinding.BestDealsRvItemBinding

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealViewHolder>() {

    inner class BestDealViewHolder(private val binding: BestDealsRvItemBinding) :
        ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * (product.price)
                    tvNewPrice.text =
                        "Rs. ${String.format("%.2f", priceAfterOffer)}"  // Rs. 13.93456 to Rs. 3.93
                    tvOldPrice.paintFlags =
                        Paint.STRIKE_THRU_TEXT_FLAG // strike through the text (old price)
                }
                tvOldPrice.text = "Rs. ${product.price}"
                tvDealProductName.text = product.name
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealViewHolder {
        return BestDealViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

}