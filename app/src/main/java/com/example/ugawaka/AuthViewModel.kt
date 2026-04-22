package com.example.ugawaka

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    var selectedRole by mutableStateOf("Client")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    fun onRoleSelected(role: String) {
        selectedRole = role
    }

    fun onEmailChanged(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        password = newPassword
    }

    fun login(onSuccess: () -> Unit) {
        // Here you would typically add validation and call a repository/use case
        if (email.isNotBlank() && password.isNotBlank()) {
            onSuccess()
        }
    }
}
