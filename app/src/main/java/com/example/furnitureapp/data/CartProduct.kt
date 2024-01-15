package com.example.furnitureapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
* To add the product in the Cart and a new sub-collection is made inside
* user's collection.
*/

@Parcelize
data class CartProduct(
    val product: Product,
    var quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null
) :Parcelable {
    fun copy(quantity: Int): Int {
        val newQuantity = quantity
        this.quantity = newQuantity
        return newQuantity
    }

    constructor() : this(Product(), 1, null, null)

}

