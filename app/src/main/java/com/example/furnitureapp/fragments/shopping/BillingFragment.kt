package com.example.furnitureapp.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furnitureapp.R
import com.example.furnitureapp.adapters.AddressAdapter
import com.example.furnitureapp.adapters.BillingProductsAdapter
import com.example.furnitureapp.data.Address
import com.example.furnitureapp.data.CartProduct
import com.example.furnitureapp.data.orders.Order
import com.example.furnitureapp.data.orders.OrderStatus
import com.example.furnitureapp.databinding.FragmentBillingBinding
import com.example.furnitureapp.util.HorizontalItemDecoration
import com.example.furnitureapp.util.Resource
import com.example.furnitureapp.util.formatPrice
import com.example.furnitureapp.util.hideBottomNavigationView
import com.example.furnitureapp.viemodel.BillingViewModel
import com.example.furnitureapp.viemodel.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment : Fragment(R.layout.fragment_billing) {

    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    private val orderViewModel by viewModels<OrderViewModel>()
    private val args by navArgs<BillingFragmentArgs>()

    private var selectedAddress: Address? = null

    private var totalPrice = 0f
    private var billingProductList = emptyList<CartProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Collecting list of products from cart Fragment and total price to display on billingFragment
        totalPrice = args.totalPrice
        billingProductList = args.billingProducts.toList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBillingProductsRV()
        setUpAddressRV()

        // Navigate back to Cart Fragment
        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }

        // Add new address navigate to addressFragment
        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        // UI changes observing : Load the Address List or Error
        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Error : ${it.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }

        // UI changes observing : Displays the SnackBar - Order Placed!
        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(
                            requireView(),
                            "Your Order Placed Successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        // Load the Billing Products and set the Total price as received from cart
        billingProductsAdapter.differ.submitList(billingProductList)
        binding.tvTotalPrice.text = totalPrice.formatPrice()

        // To select the address from the address list
        addressAdapter.onClick = {
            selectedAddress = it
        }

        // Showing the dialog for placing order confirmation if address is selected
        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "Please select the Address", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }
    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order Items")
            setMessage("Are you sure you want to order your cart items?")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            // Calls the place order function to add the order in the firestore when OK clicked on the dialog
            setPositiveButton("Ok") { dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    products = billingProductList,
                    address = selectedAddress!!,
                    totalPrice = totalPrice,
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setUpAddressRV() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setUpBillingProductsRV() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = billingProductsAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}