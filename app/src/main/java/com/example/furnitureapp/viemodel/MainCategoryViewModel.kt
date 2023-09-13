package com.example.furnitureapp.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureapp.data.Product
import com.example.furnitureapp.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealProduct: StateFlow<Resource<List<Product>>> = _bestDealProduct

    private val _bestProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProduct: StateFlow<Resource<List<Product>>> = _bestProduct

    init {
        fetchSpecialProducts()  // called inside the init block because we always want to fetch the main category/Home fragment
        fetchBestDeals()
        fetchBestProducts()
    }

    private fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        fireStore.collection("Products").whereEqualTo("category", "Special Products").get()
            .addOnSuccessListener { results ->
                val specialProductList =
                    results.toObjects(Product::class.java)  // convert results to the product type object
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun fetchBestDeals() {
        viewModelScope.launch {
            _bestDealProduct.emit(Resource.Loading())
        }
        fireStore.collection("Products").whereEqualTo("category", "Best Deals").get()
            .addOnSuccessListener { results ->
                val bestDealsProducts =
                    results.toObjects(Product::class.java)  // convert results to the product type object
                viewModelScope.launch {
                    _bestDealProduct.emit(Resource.Success(bestDealsProducts))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestDealProduct.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun fetchBestProducts() {
        viewModelScope.launch {
            _bestProduct.emit(Resource.Loading())
        }
        //fireStore.collection("Products").whereEqualTo("category", "Best Deals").get()
        fireStore.collection("Products").get()
            .addOnSuccessListener { results ->
                val bestProducts =
                    results.toObjects(Product::class.java)  // convert results to the product type object
                viewModelScope.launch {
                    _bestProduct.emit(Resource.Success(bestProducts))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestProduct.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}