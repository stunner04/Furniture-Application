package com.example.furnitureapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val images: List<String>
) : Parcelable
{
    /*
    * A secondary constructor with default values, allowing you to create instances of the Product class
    *  with default property values when no arguments are provided.
    * */
    constructor() :  this("0","","",0f, images = emptyList())
}
