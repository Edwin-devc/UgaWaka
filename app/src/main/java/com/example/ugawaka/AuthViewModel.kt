package com.example.ugawaka

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID

class AuthViewModel(private val repository: IProviderRepository) : ViewModel() {
    var selectedRole by mutableStateOf("Client")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var fullName by mutableStateOf("")
        private set

    var phoneNumber by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    var authError by mutableStateOf<String?>(null)
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

    fun onFullNameChanged(newName: String) {
        fullName = newName
    }

    fun onPhoneNumberChanged(newPhone: String) {
        phoneNumber = newPhone
    }

    fun onConfirmPasswordChanged(newPassword: String) {
        confirmPassword = newPassword
    }

    fun login(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            authError = "Please enter email and password"
            return
        }

        viewModelScope.launch {
            val account = repository.getAccountByEmail(email)
            if (account != null && account.role == selectedRole) {
                authError = null
                onSuccess()
            } else {
                authError = "Invalid email or role for this account"
            }
        }
    }

    fun signUp(onSuccess: () -> Unit) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank() || phoneNumber.isBlank()) {
            authError = "Please fill in all fields"
            return
        }
        if (password != confirmPassword) {
            authError = "Passwords do not match"
            return
        }

        viewModelScope.launch {
            val existing = repository.getAccountByEmail(email)
            if (existing != null) {
                authError = "Account with this email already exists"
            } else {
                val newAccount = DemoAccount(
                    id = UUID.randomUUID().toString(),
                    name = fullName,
                    email = email,
                    role = selectedRole
                )
                // We need to add insertAccount to repository/interface if not there
                // For now, assuming initializeData or similar handles it or we add it.
                // Let's add it to the interface.
                (repository as? ProviderRepository)?.insertAccount(newAccount)
                authError = null
                onSuccess()
            }
        }
    }
}