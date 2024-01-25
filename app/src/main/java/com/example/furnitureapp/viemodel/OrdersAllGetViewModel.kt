package com.example.furnitureapp.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class OrdersAllGetViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    // State Observer for the List of order
    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val allOrders = _allOrders.asStateFlow()

    // Called when instance of this class created for first time to setUp the data initially
    init {
        getAllOrders()
    }

    //Retrieve all orders of a user from the firestore
    fun getAllOrders() {
        viewModelScope.launch {
            _allOrders.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!).collection("order").get()
            .addOnSuccessListener {
                val orders = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _allOrders.emit(Resource.Success(orders))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _allOrders.emit(Resource.Error(it.message.toString())) }
            }
    }
}