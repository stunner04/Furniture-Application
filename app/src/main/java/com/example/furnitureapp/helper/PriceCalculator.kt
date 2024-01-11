package com.example.furnitureapp.helper

fun Float?.getProductPrice(price: Float): Float {
    if (this == null) // if price is null
        return price
    val remainingPricePercentage = 1f - this
    val priceAfterOffer = remainingPricePercentage * price
    return priceAfterOffer
}