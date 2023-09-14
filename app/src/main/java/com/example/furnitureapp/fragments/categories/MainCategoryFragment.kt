package com.example.furnitureapp.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.furnitureapp.R
import com.example.furnitureapp.adapters.BestDealsAdapter
import com.example.furnitureapp.adapters.BestProductAdapter
import com.example.furnitureapp.adapters.SpecialProductsAdapter
import com.example.furnitureapp.databinding.FragmentMainCategoryBinding
import com.example.furnitureapp.util.Resource
import com.example.furnitureapp.viemodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "MainCategoryFragment"

@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductAdapter: BestProductAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSpecialProductsRV()
        setUpBestDealsRV()
        setUpBestProductsRV()

        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        specialProductAdapter.differ.submitList(it.data)  // updating the Adapter with listsOfProducts from fireStore
                        hideLoading()
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.bestProductProgressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        bestProductAdapter.differ.submitList(it.data)  // updating the Adapter with listsOfProducts from fireStore
                        binding.bestProductProgressBar.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.bestProductProgressBar.visibility = View.GONE
                    }

                    else -> Unit
                }
            }
            // detecting the 10 items loaded successfully and next 20 items need to get called when reaches the end
            binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{v,_,scrollY,_,_ ->
               // Log.i("MainFraGment","${"SCROLLING reached"}")
                 if(v.getChildAt(0).bottom <= v.height + scrollY)
                 {
                     //Log.i("MainFraGment","${"SCROLLED DETECTED"}")
                    viewModel.fetchBestProducts()
                 }
            })
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        bestDealsAdapter.differ.submitList(it.data)  // updating the Adapter with listsOfProducts from fireStore
                        hideLoading()
                    }

                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setUpBestProductsRV() {
        bestProductAdapter = BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter
        }
    }

    private fun setUpBestDealsRV() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun setUpSpecialProductsRV() {
        specialProductAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductAdapter
        }
    }

    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility = View.VISIBLE
    }

}