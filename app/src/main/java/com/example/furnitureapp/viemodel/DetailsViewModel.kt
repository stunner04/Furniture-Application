package com.example.furnitureapp.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureapp.data.CartProduct
import com.example.furnitureapp.firebase.FirebaseCommon
import com.example.furnitureapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateProductInCart(cartProduct: CartProduct)
            /*
            * Two things performed by this functions mentioned below :
            *    -> To increment the quantity of product if same product is selected by user
            *    -> To add a new product to the sub collection of the user if selected product is first time added.
            */ {
        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())
        }
        fireStore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id).get()// Comparison between the product already present and product to be added
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()) { // If Sub Collection is empty first then add a new collection to it

                        addNewProduct(cartProduct)
                    } else {
                        // Getting the first product document added in the list and comparing with parameter passed cartProduct
                        val product = it.first().toObject(CartProduct::class.java)

                        if (product == cartProduct) // Increment the already selected product quantity
                        {
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)
                        } else // Add the new selected product
                        {
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }
    }

    fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { addedProduct, e ->
            viewModelScope.launch {
                if (e == null) {
                    _addToCart.emit(Resource.Success(addedProduct!!))
                } else {
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId) { _, e ->
            viewModelScope.launch {
                if (e == null) {
                    _addToCart.emit(Resource.Success(cartProduct))
                } else {
                    _addToCart.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }
}