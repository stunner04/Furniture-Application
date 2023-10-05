package com.example.furnitureapp.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.furnitureapp.data.Category
import com.example.furnitureapp.util.Resource
import com.example.furnitureapp.viemodel.CategoryViewModel
import com.example.furnitureapp.viemodel.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class ChairFragment : BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore
    val viewModel by viewModels<CategoryViewModel>() {
        BaseCategoryViewModelFactory(firestore, Category.Chair)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        offerAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                        hideOfferLoading()
                    }

                    is Resource.Loading -> {
                        showOfferLoading()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        bestProductAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                        hideBestProductsLoading()
                    }

                    is Resource.Loading -> {
                        showBestProductsLoading()
                    }

                    else -> Unit
                }
            }
        }
    }

    override fun onBestProductPagingRequest() {

    }

    override fun onOfferProductPagingRequest() {

    }
}