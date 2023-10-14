package com.example.furnitureapp.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureapp.data.Category
import com.example.furnitureapp.data.Product
import com.example.furnitureapp.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/*
* Category viewmodel is for the Basecategory fragment whose instance(viewmodel) is used in child fragment like chair
* to display items.
* */

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category : Category
) :ViewModel() {

    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts = _bestProducts.asStateFlow()

    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts()
    {
      viewModelScope.launch {
          _offerProducts.emit(Resource.Loading())
      }
        /*
        Fetching the product which are having the offer percentage only from the Products in firestore
        */
        firestore.collection("Products").whereEqualTo("category",category.Category)
            .whereNotEqualTo("offerPercentage",null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(products))  // storing the list of product from firestore to offerProduct.
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts()
    {
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }
        /*
        Fetching the product which are not having the offer percentage only , from the Products in firestore
        */
        firestore.collection("Products").whereEqualTo("category",category.Category)
            .whereEqualTo("offerPercentage",null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(products))  // storing the list of product from firestore to offerProduct.
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}