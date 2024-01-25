package com.example.furnitureapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furnitureapp.adapters.OrdersAllAdapter
import com.example.furnitureapp.databinding.FragmentOrdersBinding
import com.example.furnitureapp.util.Resource
import com.example.furnitureapp.viemodel.OrdersAllGetViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OrdersAllFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    private val ordersViewModel by viewModels<OrdersAllGetViewModel>()
    private val ordersAllAdapter by lazy { OrdersAllAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOrdersAllRV()

        // Image closing navigate back to previous screen
        binding.imageCloseOrders.setOnClickListener {
            findNavController().navigateUp()
        }

        // Submitting the list of orders from firestore to RV to display OR if 0 orders then show empty image
        lifecycleScope.launchWhenStarted {
            ordersViewModel.allOrders.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        // Set the order data in the OrdersRV
                        ordersAllAdapter.differ.submitList(it.data)
                        // If a new user or Order is empty in the firestore shows empty
                        if (it.data.isNullOrEmpty()) {
                            binding.tvEmptyOrders.visibility = View.VISIBLE
                        }
                    }

                    is Resource.Error -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }

    }

    // SetUp OrdersRV
    private fun setUpOrdersAllRV() {
        binding.rvAllOrders.apply {
            adapter = ordersAllAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }
}