package com.example.furnitureapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.furnitureapp.R
import com.example.furnitureapp.adapters.HomeViewpagerAdapter
import com.example.furnitureapp.databinding.FragmentHomeBinding
import com.example.furnitureapp.fragments.categories.AccessoryFragment
import com.example.furnitureapp.fragments.categories.ChairFragment
import com.example.furnitureapp.fragments.categories.CupboardFragment
import com.example.furnitureapp.fragments.categories.FurnitureFragment
import com.example.furnitureapp.fragments.categories.MainCategoryFragment
import com.example.furnitureapp.fragments.categories.TableFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        binding.viewPagerHome.isUserInputEnabled =
            false // cancels the swipe behaviour of viewpager with the tablayout

        val viewpager2Adapter =
            HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewPagerHome.adapter = viewpager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome)
        { tab, position ->
            when (position) {
                0 -> tab.text = "Home"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cupboard"
                3 -> tab.text = "Table"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Furniture"
            }
        }.attach()
    }
}