package com.example.furnitureapp.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureapp.data.Categories
import com.example.furnitureapp.data.Product
import com.example.furnitureapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _search = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val search = _search.asStateFlow()

    private val _categories = MutableStateFlow<Resource<List<Categories>>>(Resource.Unspecified())
    val categories = _categories.asStateFlow()

    init {
        getCategories()
    }

    fun searchGetProducts(searchQuery: String) {
        viewModelScope.launch {
            _search.emit(Resource.Loading())
        }
        firestore.collection("Products").orderBy("name")
            .startAt(searchQuery)
            .endAt("\u03A9+$searchQuery")
            .limit(5)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    val productsList = it.result!!.toObjects(Product::class.java)
                    viewModelScope.launch {
                        _search.emit(Resource.Success(productsList))
                    }
                } else {
                    viewModelScope.launch {
                        _search.emit(Resource.Error(it.exception?.message.toString()))
                    }
                }
            }
    }

    private var categoriesSafe: List<Categories>? = null

    fun getCategories() {
        if (categoriesSafe != null) {
            viewModelScope.launch {
                _categories.emit(Resource.Success(categoriesSafe))
            }
        }
        viewModelScope.launch {
            _categories.emit(Resource.Loading())
        }
        firestore.collection("categories").orderBy("rank").get().addOnSuccessListener {
            val categoriesList = it.toObjects(Categories::class.java)
            categoriesSafe = categoriesList
            viewModelScope.launch {
                _categories.emit(Resource.Success(categoriesSafe))
            }
        }
            .addOnFailureListener {
                viewModelScope.launch {
                    _categories.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}