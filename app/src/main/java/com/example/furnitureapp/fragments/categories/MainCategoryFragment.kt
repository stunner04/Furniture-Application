package com.example.furnitureapp.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.furnitureapp.R
import com.example.furnitureapp.adapters.SpecialProductsAdapter
import com.example.furnitureapp.databinding.FragmentMainCategoryBinding

class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {
    private lateinit var binding : FragmentMainCategoryBinding
    private lateinit var specialProductAdapter : SpecialProductsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSpecialProductsRV()
    }

    private fun setUpSpecialProductsRV() {
        specialProductAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter=specialProductAdapter
        }
    }
}