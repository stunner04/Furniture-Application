package com.example.furnitureapp.util

import android.util.Patterns

fun validateEmail(email: String): RegisterValidation {

    if (email.isEmpty()) {
        return RegisterValidation.Failed("Email can't be Empty!")
    }
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return RegisterValidation.Failed("Wrong Format")
    }
    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation {

    if (password.isEmpty()) {
        return RegisterValidation.Failed("Password can't be Empty!")
    }
    if (password.length < 6) {
        return RegisterValidation.Failed("Password must contain 6 characters")
    }
    return RegisterValidation.Success
}