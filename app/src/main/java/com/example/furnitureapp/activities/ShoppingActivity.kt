package com.example.furnitureapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.furnitureapp.R
import com.example.furnitureapp.databinding.ActivityShoppingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

   private val binding by lazy {
       ActivityShoppingBinding.inflate(layoutInflater)
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.shoppingNavHostFragment) as NavHostFragment

        val navController = navHostFragment.navController

        binding.shoppingBottomNavigation.setupWithNavController(navController)
    }
}