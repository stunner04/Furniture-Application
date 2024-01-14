package com.example.furnitureapp.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.furnitureapp.data.CartProduct
import com.example.furnitureapp.databinding.BillingProductsRvItemBinding
import com.example.furnitureapp.helper.getProductPrice
import com.example.furnitureapp.util.formatPrice

class BillingProductsAdapter :
    RecyclerView.Adapter<BillingProductsAdapter.BillingProductsViewHolder>() {

    inner class BillingProductsViewHolder(val binding: BillingProductsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(billingProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text = billingProduct.product.name
                tvBillingProductQuantity.text = billingProduct.quantity.toString()

                val priceAfterOffer =
                    billingProduct.product.offerPercentage.getProductPrice(billingProduct.product.price)
                tvProductCartPrice.text = priceAfterOffer.formatPrice()

                imageCartProductColor.setImageDrawable(
                    ColorDrawable(
                        billingProduct.selectedColor ?: Color.TRANSPARENT
                    )
                )
                tvCartProductSize.text = billingProduct.selectedSize ?: "".also {
                    imageCartProductSize.setImageDrawable(
                        ColorDrawable(Color.TRANSPARENT)
                    )
                }
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsViewHolder {
        return BillingProductsViewHolder(
            BillingProductsRvItemBinding
                .inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: BillingProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

}