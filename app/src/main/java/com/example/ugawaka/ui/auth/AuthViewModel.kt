package com.example.ugawaka.ui.auth

import androidx.lifecycle.AndroidViewModel
import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.ugawaka.data.local.SessionManager
import com.example.ugawaka.data.remote.LoginRequest
import com.example.ugawaka.data.remote.RegisterRequest
import com.example.ugawaka.data.remote.LoginResponse
import com.example.ugawaka.data.remote.RetrofitClient
import com.example.ugawaka.data.repository.UgaWakaRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager.getInstance(application)
    var selectedRole by mutableStateOf("Client")
        private set

    var fullName by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var phoneNumber by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    var nin by mutableStateOf("")
        private set

    var serviceId by mutableStateOf<Int?>(null)
        private set

    var rate by mutableStateOf("")
        private set

    var about by mutableStateOf("")
        private set

    private val repository = UgaWakaRepository()
    val services = repository.services

    init {
        viewModelScope.launch {
            repository.fetchServices()
        }
    }

    var latitude by mutableStateOf<Double?>(null)
        private set

    var longitude by mutableStateOf<Double?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onRoleSelected(role: String) {
        selectedRole = role
    }

    fun onFullNameChanged(newName: String) {
        fullName = newName
    }

    fun onEmailChanged(newEmail: String) {
        email = newEmail
    }

    fun onPhoneChanged(newPhone: String) {
        phoneNumber = newPhone
    }

    fun onPasswordChanged(newPassword: String) {
        password = newPassword
    }

    fun onConfirmPasswordChanged(newPassword: String) {
        confirmPassword = newPassword
    }

    fun onNinChanged(newNin: String) {
        if (newNin.length <= 14 && newNin.all { it.isLetterOrDigit() }) {
            nin = newNin.uppercase()
        }
    }

    fun onServiceSelected(id: Int) {
        serviceId = id
    }

    fun onRateChanged(newRate: String) {
        rate = newRate
    }

    fun updateLocation(lat: Double, lon: Double) {
        latitude = lat
        longitude = lon
    }

    fun login(onSuccess: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please enter email and password"
            return
        }

        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                // Initialize CSRF if needed
                val csrfResponse = RetrofitClient.authService.initializeCsrf()
                if (!csrfResponse.isSuccessful) {
                    errorMessage = "Session init failed: ${csrfResponse.code()}"
                    isLoading = false
                    return@launch
                }

                val request = LoginRequest(email = email, password = password)
                val response = if (selectedRole == "Provider") {
                    RetrofitClient.authService.providerLogin(request)
                } else {
                    RetrofitClient.authService.customerLogin(request)
                }

                Log.d("AuthViewModel", "Response Code: ${response.code()}")
                val body = response.body()
                Log.d("AuthViewModel", "Response Body: $body")

                if (response.isSuccessful) {
                    // Check for success flag OR presence of a token/user
                    if (body?.token != null || body?.customer != null || body?.provider != null) {
                        val token = body.token ?: ""
                        val name = body.customer?.name ?: body.provider?.name ?: "User"
                        val emailAddress = body.customer?.email ?: body.provider?.email ?: ""
                        
                        sessionManager.saveAuthToken(token)
                        sessionManager.saveUserName(name)
                        sessionManager.saveUserEmail(emailAddress)
                        sessionManager.saveUserRole(selectedRole)
                        
                        RetrofitClient.authToken = token
                        RetrofitClient.userName = name
                        
                        onSuccess(selectedRole)
                    } else {
                        // Logic failure with 200 OK
                        val errors = body?.errors
                        val msg = if (errors != null && errors.isNotEmpty()) {
                            errors.values.flatten().joinToString("\n")
                        } else {
                            body?.message ?: "Login failed (No message)"
                        }
                        errorMessage = msg
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    Log.e("AuthViewModel", "Error JSON: $errorJson")
                    
                    errorMessage = try {
                        val errorResponse = Gson().fromJson(errorJson, LoginResponse::class.java)
                        val msg = errorResponse.message
                        val errors = errorResponse.errors
                        
                        if (errors != null && errors.isNotEmpty()) {
                            errors.values.flatten().joinToString("\n")
                        } else {
                            msg ?: "Invalid credentials"
                        }
                    } catch (e: Exception) {
                        "An unexpected error occurred. Please try again."
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Exception during login", e)
                errorMessage = "Connection error: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                isLoading = false
            }
        }
    }

    fun onAboutChanged(newAbout: String) {
        about = newAbout
    }

    fun signUp(onSuccess: (String) -> Unit) {
        if (email.isBlank() || password.isBlank() || fullName.isBlank() || phoneNumber.isBlank()) {
            errorMessage = "Please fill all fields"
            return
        }
        if (password != confirmPassword) {
            errorMessage = "Passwords do not match"
            return
        }
        if (selectedRole == "Provider") {
            if (nin.length != 14) {
                errorMessage = "NIN must be exactly 14 characters"
                return
            }
            if (serviceId == null) {
                errorMessage = "Please select a service"
                return
            }
            if (rate.isBlank()) {
                errorMessage = "Please enter your base rate"
                return
            }
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                // Initialize CSRF
                RetrofitClient.authService.initializeCsrf()

                val request = RegisterRequest(
                    name = fullName,
                    email = email,
                    phone_number = phoneNumber,
                    password = password,
                    password_confirmation = confirmPassword,
                    latitude = latitude,
                    longitude = longitude,
                    nin = if (selectedRole == "Provider") nin else null,
                    service_id = if (selectedRole == "Provider") serviceId else null,
                    rate = if (selectedRole == "Provider") rate else null,
                    about = if (selectedRole == "Provider") about else null
                )

                val response = if (selectedRole == "Provider") {
                    RetrofitClient.authService.providerRegister(request)
                } else {
                    RetrofitClient.authService.customerRegister(request)
                }

                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.token ?: ""
                    val name = body?.customer?.name ?: body?.provider?.name ?: fullName
                    val email = body?.customer?.email ?: body?.provider?.email ?: ""
                    
                    if (selectedRole != "Provider") {
                        sessionManager.saveAuthToken(token)
                        sessionManager.saveUserName(name)
                        sessionManager.saveUserEmail(email)
                        sessionManager.saveUserRole(selectedRole)
                        
                        RetrofitClient.authToken = token
                        RetrofitClient.userName = name
                    } else {
                        // For providers, we don't save the session because they are not approved yet
                        // We might want to clear any existing session just in case
                        sessionManager.clearSession()
                        RetrofitClient.authToken = null
                    }

                    onSuccess(selectedRole)
                } else {
                    val errorJson = response.errorBody()?.string()
                    errorMessage = try {
                        val errorResponse = Gson().fromJson(errorJson, LoginResponse::class.java)
                        val errors = errorResponse.errors
                        if (errors != null && errors.isNotEmpty()) {
                            errors.values.flatten().joinToString("\n")
                        } else {
                            errorResponse.message ?: "Registration failed"
                        }
                    } catch (e: Exception) {
                        "Registration failed. Please try again."
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Connection error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
