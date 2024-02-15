package com.example.furnitureapp.fragments.shopping

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.furnitureapp.R
import com.example.furnitureapp.activities.ShoppingActivity
import com.example.furnitureapp.adapters.BillingProductsAdapter
import com.example.furnitureapp.adapters.CategoriesAdapter
import com.example.furnitureapp.adapters.SearchAdapter
import com.example.furnitureapp.databinding.FragmentSearchBinding
import com.example.furnitureapp.util.Resource
import com.example.furnitureapp.util.VerticalItemDecoration
import com.example.furnitureapp.viemodel.AddressViewModel
import com.example.furnitureapp.viemodel.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding
    private val searchAdapter by lazy { SearchAdapter() }
    private val categoriesAdapter by lazy { CategoriesAdapter() }
    private val viewModel by viewModels<SearchViewModel>()
    private lateinit var inputMethodManger: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // viewModel.getCategories()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryRecyclerView()
        setUpSearchRV()
        showKeyboardAutomatically()
        onHomeClick()
        searchProducts()
        onSearchTextClick()
        onCancelTvClick()
        hideBottomWhenKeyBoardAppears()

        binding.apply {
            frameScan.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "This feature is not available yet",
                    Toast.LENGTH_SHORT
                ).show()
            }
            fragmeMicrohpone.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "This feature is not available yet",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.categories.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showCategoriesLoading()
                    }

                    is Resource.Success -> {
                        hideCategoriesLoading()
                        val categories = it.data
                        categoriesAdapter.differ.submitList(categories?.toList())
                    }

                    is Resource.Error -> {
                        hideCategoriesLoading()
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.search.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarCategories.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarCategories.visibility = View.GONE
                        val products = it.data
                        searchAdapter.differ.submitList(products)
                        showCancelTv()
                    }

                    is Resource.Error -> {
                        binding.progressbarCategories.visibility = View.GONE
                        showCancelTv()
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }

    }

    private fun setUpSearchRV() {
        binding.rvSearch.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = searchAdapter
        }
    }

    private fun setupCategoryRecyclerView() {
        binding.rvCategories.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = categoriesAdapter
            addItemDecoration(VerticalItemDecoration(40))
        }
    }

    private fun hideBottomWhenKeyBoardAppears() {
        val rootView = view?.findViewById<NestedScrollView>(R.id.rootId)
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.shoppingBottomNavigation)

        rootView?.viewTreeObserver?.addOnPreDrawListener {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val isKeyboardVisible = imm.isAcceptingText
            bottomNav?.visibility = if (isKeyboardVisible) View.GONE else View.VISIBLE
            true
        }
    }

    private fun showKeyboardAutomatically() {
        inputMethodManger =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManger.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
        binding.edSearch.requestFocus()
    }


    private fun onHomeClick() {
        val btm = activity?.findViewById<BottomNavigationView>(R.id.shoppingBottomNavigation)
        btm?.menu?.getItem(0)?.setOnMenuItemClickListener {
            activity?.onBackPressed()
            true
        }
    }

    var job: Job? = null
    private fun searchProducts() {
        binding.edSearch.addTextChangedListener { query ->
            val queryTrim = query.toString().trim()
            if (queryTrim.isNotEmpty()) {
                val searchQuery = query.toString().substring(0, 1).toUpperCase()
                    .plus(query.toString().substring(1))
                job?.cancel()
                job = CoroutineScope(Dispatchers.IO).launch {
                    delay(500L)
                    viewModel.searchGetProducts(searchQuery)
                }
            } else {
                searchAdapter.differ.submitList(emptyList())
                hideCancelTv()
            }
        }
    }

    private fun onSearchTextClick() {
        searchAdapter.onItemClick = { product ->
            val bundle = Bundle()
            bundle.putParcelable("product", product)
            /**
             * Hide the keyboard
             */
            val imm =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.hideSoftInputFromWindow(requireView().windowToken, 0)

            findNavController().navigate(
                R.id.action_searchFragment_to_productDetailsFragment,
                bundle
            )
        }
    }

    private fun onCancelTvClick() {
        binding.tvCancel.setOnClickListener {
            searchAdapter.differ.submitList(emptyList())
            binding.edSearch.setText("")
            hideCancelTv()
        }
    }

    private fun hideCategoriesLoading() {
        binding.progressbarCategories.visibility = View.GONE

    }

    private fun showCategoriesLoading() {
        binding.progressbarCategories.visibility = View.VISIBLE

    }

    private fun showCancelTv() {
        binding.tvCancel.visibility = View.VISIBLE
        binding.imgMic.visibility = View.GONE
        binding.imgScan.visibility = View.GONE
        binding.fragmeMicrohpone.visibility = View.GONE
        binding.frameScan.visibility = View.GONE
    }

    private fun hideCancelTv() {
        binding.tvCancel.visibility = View.GONE
        binding.imgMic.visibility = View.VISIBLE
        binding.imgScan.visibility = View.VISIBLE
        binding.fragmeMicrohpone.visibility = View.VISIBLE
        binding.frameScan.visibility = View.VISIBLE
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.edSearch.clearFocus()
    }

    override fun onResume() {
        super.onResume()
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.shoppingBottomNavigation)
        bottomNav?.visibility = View.VISIBLE
    }


}