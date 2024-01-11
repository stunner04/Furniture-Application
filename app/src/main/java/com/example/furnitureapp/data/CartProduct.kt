package com.example.furnitureapp.data

/*
* To add the product in the Cart and a new sub-collection is made inside
* user's collection.
*/

class CartProduct(
    val product: Product,
    var quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null
) {
    fun copy(quantity: Int): Int {
        val newQuantity = quantity
        this.quantity = newQuantity
        return newQuantity
    }

    constructor() : this(Product(), 1, null, null)

    /* Extension function FOR COPY() USED IN FirebaseCommon class */
//    companion object {
//        fun CartProduct.copy(quantity: Int): CartProduct {
//            return CartProduct(
//                this.product.copy(), // Make a copy of the product object
//                quantity,
//                this.selectedColor,
//                this.selectedSize
//            )
//        }
//    }


}

