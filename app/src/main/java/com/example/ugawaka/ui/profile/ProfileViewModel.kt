package com.example.ugawaka.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.ugawaka.data.local.SessionManager
import com.example.ugawaka.data.repository.UgaWakaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel(application: Application, private val repository: UgaWakaRepository = UgaWakaRepository()) : AndroidViewModel(application) {
    private val sessionManager = SessionManager.getInstance(application)

    private val _userName = MutableStateFlow(sessionManager.fetchUserName() ?: "User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow(sessionManager.fetchUserEmail() ?: "user@example.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    fun logout() {
        sessionManager.clearSession()
        com.example.ugawaka.data.remote.RetrofitClient.authToken = null
        com.example.ugawaka.data.remote.RetrofitClient.userName = "User"
    }
}
