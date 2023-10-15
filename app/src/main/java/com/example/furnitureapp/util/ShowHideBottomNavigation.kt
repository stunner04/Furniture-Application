package com.example.furnitureapp.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.furnitureapp.R
import com.example.furnitureapp.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView() {
    val bottomNavigation =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.shoppingBottomNavigation)
    bottomNavigation.visibility = View.GONE
}

fun Fragment.showBottomNavigationView() {
    val bottomNavigation =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.shoppingBottomNavigation)
    bottomNavigation.visibility = View.VISIBLE
}