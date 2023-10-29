package com.example.furnitureapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.adapters.LinearLayoutBindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.furnitureapp.R
import com.example.furnitureapp.activities.ShoppingActivity
import com.example.furnitureapp.adapters.ColorsAdapter
import com.example.furnitureapp.adapters.SizesAdapter
import com.example.furnitureapp.adapters.ViewPager2ImagesAdapter
import com.example.furnitureapp.data.CartProduct
import com.example.furnitureapp.databinding.FragmentProductDetailsBinding
import com.example.furnitureapp.util.Resource
import com.example.furnitureapp.util.hideBottomNavigationView
import com.example.furnitureapp.viemodel.DetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPager2ImagesAdapter by lazy { ViewPager2ImagesAdapter() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }

    // For ADD TO CART functionality
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewmodel by viewModels<DetailsViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Source of data for this fragment i.e, RV's & TV's from the parceled product class
        val product = args.product

        // Setup the recyclerview
        setupSizesRV()
        setupColorsRV()
        setupViewPager()

        // close image button
        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        // selecting the size on clicking the sizes RV list
        sizesAdapter.onItemClick ={
            selectedSize = it
        }

        // selecting the color on clicking the colors RV list
        colorsAdapter.onItemClick = {
            selectedColor = it
        }

        // Click Listener for the ADD TO CART btn
        binding.btnAddToCart.setOnClickListener {
            // We can add If else statements for the ENFORCING THE SELECTION of the products then it will be added only.
            viewmodel.addUpdateProductInCart(CartProduct(product,1,selectedColor,selectedSize))
        }

        lifecycleScope.launchWhenStarted {
            viewmodel.addToCart.collectLatest {
                when(it)
                {
                    is Resource.Loading -> {
                        binding.btnAddToCart.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnAddToCart.revertAnimation()
                        binding.btnAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                    }
                    is Resource.Error -> {
                        binding.btnAddToCart.stopAnimation()
                        Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // Display the data by submitting the list to RV and setup the textview.
        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "Rs. ${product.price}"
            tvProductDescription.text = product.description

            if (product.colors.isNullOrEmpty()) {
                tvProductColors.visibility = View.INVISIBLE
            }
            if (product.sizes.isNullOrEmpty()) {
                tvProductSize.visibility = View.INVISIBLE
            }
        }
        viewPager2ImagesAdapter.differ.submitList(product.images)
        product.colors?.let { colorsAdapter.differ.submitList(it) } // if color is not optional/empty then execute the let block
        product.sizes?.let { sizesAdapter.differ.submitList(it) } // if size is not optional/empty then execute the let block

    }

    private fun setupViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPager2ImagesAdapter
        }
    }

    private fun setupColorsRV() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSizesRV() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}