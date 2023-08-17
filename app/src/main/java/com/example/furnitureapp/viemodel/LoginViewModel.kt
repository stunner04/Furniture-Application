package com.example.furnitureapp.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.furnitureapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>() // FOR ONE TIME EVENT navigation
    val login = _login.asSharedFlow()  // asSharedFlow converts to immutable sharedFlow

    private val _resetPassword = MutableSharedFlow<Resource<String>>() // FOR ONE TIME EVENT Snack bar
    val resetPassword = _resetPassword.asSharedFlow() // asSharedFlow converts to immutable sharedFlow

    fun login(email: String, password: String)
    {
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }
        firebaseAuth.signInWithEmailAndPassword(
            email, password)
            .addOnSuccessListener {
            viewModelScope.launch {
                // emit() is the suspended  function so coroutine is needed
                it.user?.let {
                    _login.emit(Resource.Success(it))
                }
            }
        }
            .addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun resetPassword(email:String)
    {
      viewModelScope.launch {
          _resetPassword.emit(Resource.Loading())
      }
          firebaseAuth.sendPasswordResetEmail(email)
              .addOnSuccessListener {
                  viewModelScope.launch {
                      _resetPassword.emit(Resource.Success(email))
                  }
              }
              .addOnFailureListener {
                  viewModelScope.launch {
                      _resetPassword.emit(Resource.Error(it.message.toString()))
                  }
              }
      }

    }

