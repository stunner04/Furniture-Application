package com.example.furnitureapp.data.orders

sealed class OrderStatus(val status : String) {

    object Ordered : OrderStatus("Ordered")
    object Confirmed : OrderStatus("Confirmed")
    object Shipped : OrderStatus("Shipped ")
    object Delivered : OrderStatus("Delivered")
    object Cancelled : OrderStatus("Cancelled")
    object Returned : OrderStatus("Returned")

}