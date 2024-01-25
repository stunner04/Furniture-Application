package com.example.furnitureapp.data.orders

sealed class OrderStatus(val status: String) {
    object Ordered : OrderStatus("Ordered")
    object Confirmed : OrderStatus("Confirmed")
    object Shipped : OrderStatus("Shipped ")
    object Delivered : OrderStatus("Delivered")
    object Cancelled : OrderStatus("Cancelled")
    object Returned : OrderStatus("Returned")
}
// Object of Order Status return based on the condition of a product
fun getOrderStatus(status: String): OrderStatus {
    return when (status) {
        "Ordered" -> OrderStatus.Ordered
        "Confirmed" -> OrderStatus.Confirmed
        "Shipped" -> OrderStatus.Shipped
        "Delivered" -> OrderStatus.Delivered
        "Cancelled" -> OrderStatus.Cancelled
        else -> OrderStatus.Returned
    }
}