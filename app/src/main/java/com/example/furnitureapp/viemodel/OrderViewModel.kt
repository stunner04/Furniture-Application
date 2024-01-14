package com.example.furnitureapp.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureapp.data.CartProduct
import com.example.furnitureapp.data.orders.Order
import com.example.furnitureapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    /**
     * Run a batch to write-only in the firestore to do multiple operations at a same time :
     * Adding a order in the user-order collection
     * Adding a order in the order collection
     * Clear the User Cart after Placing the Order
     */

    // Stores the order in the firestore when places Order
    fun placeOrder(order: Order) {
        viewModelScope.launch { _order.emit(Resource.Loading()) }

        firestore.runBatch { batch ->

            // Adding a order in the user-order collection
            firestore.collection("user").document(auth.uid!!)
                .collection("order").document().set(order)

            // Adding a order in the order collection
            firestore.collection("order").document().set(order)

            // Clear the User Cart after Placing the Order
            firestore.collection("user").document(auth.uid!!)
                .collection("cart").get().addOnSuccessListener {
                    it.documents.forEach {
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch { _order.emit(Resource.Success(order)) }
        }
            .addOnFailureListener {
                viewModelScope.launch { _order.emit(Resource.Error(it.message.toString())) }
            }
    }
}
