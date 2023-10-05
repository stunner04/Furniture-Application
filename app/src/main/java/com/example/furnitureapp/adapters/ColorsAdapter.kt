package com.example.furnitureapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.furnitureapp.databinding.ColorRvItemBinding


class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() { // 1

    private var selectedPosition = -1

    inner class ColorsViewHolder(private val binding: ColorRvItemBinding) :
        ViewHolder(binding.root) //2
    { // 6
        fun bind(color: Int, positon: Int) {

            val imageDrawable =
                ColorDrawable(color) // to change the color of the circle based on the color parameter
            binding.imageColor.setImageDrawable(imageDrawable)

            if (positon == selectedPosition) // color is selected
            {
                binding.apply {
                    imageShadow.visibility = View.VISIBLE
                    imagePicked.visibility = View.VISIBLE
                }
            } else { // color is not selected
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                    imagePicked.visibility = View.INVISIBLE
                }
            }
        }
    }

    var diffCallback = object : DiffUtil.ItemCallback<Int>() // 3
    {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)//4

    //5
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        return ColorsViewHolder(ColorRvItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        val color = differ.currentList[position]
        // to bind the color to the imageview based on the passed color int value into the passed position
        holder.bind(color, position)
        //7
        holder.itemView.setOnClickListener { // to change the color position as per selection
            if (selectedPosition >= 0) {
                /*
                * To unselect the selected position
                * selectedPosition != -1 means selected and condition is true so notifyItemChanged() called
                * which invokes the bind function to rebuild the view of RV and goes in the else part to
                * make the selected color to unselect and invisible.
                */
                notifyItemChanged(selectedPosition)
            }
            /* Then we select the on clicked color and position then again view rebuild make the visible
            * color using bind function.
            */
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition) //  to refresh  the list
            onItemClick?.invoke(color)
        }
    }

    var onItemClick: ((Int) -> Unit)? = null
}