package com.example.furnitureapp.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furnitureapp.adapters.BillingProductsAdapter
import com.example.furnitureapp.data.orders.OrderStatus
import com.example.furnitureapp.data.orders.getOrderStatus
import com.example.furnitureapp.databinding.FragmentOrderDetailBinding
import com.example.furnitureapp.util.VerticalItemDecoration
import com.example.furnitureapp.util.formatPrice

class OrderDetailFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailBinding
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Getting the order details from the ordersAllFragment
        val order = args.order

        binding.apply {
            tvOrderId.text = "Order #${order.orderId}"

            // Setting steps for StepView
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )

            // Setting the indexes of stepView based on status
            val currentOrderStatus = when (getOrderStatus(order.orderStatus)) {
                OrderStatus.Ordered -> 0
                OrderStatus.Confirmed -> 1
                OrderStatus.Shipped -> 2
                OrderStatus.Delivered -> 3
                else -> 0
            }

            stepView.go(currentOrderStatus, true)

            // When delivered it shows done.
            if (currentOrderStatus == 3) {
                stepView.done(true)
            }

            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.state} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = order.totalPrice.formatPrice()
            imageCloseOrder.setOnClickListener {
                findNavController().navigateUp()
            }
        }
        setUpBillingOrderDetailRV()
        billingProductsAdapter.differ.submitList(order.products)
    }

    // SetUp RV for orderDetails
    private fun setUpBillingOrderDetailRV() {
        binding.rvProducts.apply {
            adapter = billingProductsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}