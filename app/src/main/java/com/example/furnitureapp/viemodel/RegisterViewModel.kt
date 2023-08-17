package com.example.furnitureapp.viemodel

import androidx.lifecycle.ViewModel
import com.example.furnitureapp.data.User
import com.example.furnitureapp.util.Constants
import com.example.furnitureapp.util.RegisterFieldState
import com.example.furnitureapp.util.RegisterValidation
import com.example.furnitureapp.util.Resource
import com.example.furnitureapp.util.validateEmail
import com.example.furnitureapp.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/*
In this viewModel we REGISTER ,VALIDATE & STORE the new users data in fireStore
and for authentication I used FirebaseAuthentication.Also, used the
DI for providing the instances(firebaseAuth,db) for the RegisterViewModel.
*/
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { it ->
                    it.user?.let {
                        saveUserInfo(it.uid, user) // it.uid for the unique ID of the new user
                        //_register.value = Resource.Success(it)
                    }
                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        } else {    // if not validated registration
            val registerFieldState =
                RegisterFieldState(validateEmail(user.email), validatePassword(password))
            runBlocking {
                _validation.send(registerFieldState)
            }
        }
    }

    private fun saveUserInfo(userUid: String, user: User) {  // to save info in FIRESTORE
        db.collection(Constants.USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success

        return shouldRegister
    }
}