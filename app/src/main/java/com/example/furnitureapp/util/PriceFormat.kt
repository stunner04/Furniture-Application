package com.example.furnitureapp.util

import java.text.DecimalFormat

fun Float.formatPrice():String{
    return "Rs."+ DecimalFormat("#,##0").format(this)
}