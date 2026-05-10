package com.example.ugawaka.data.remote

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone_number: String,
    val password: String,
    val password_confirmation: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val nin: String? = null,
    val service_id: Int? = null,
    val rate: String? = null,
    val about: String? = null
)

data class LoginResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val token: String? = null,
    val customer: UserDto? = null,
    val provider: UserDto? = null,
    val errors: Map<String, List<String>>? = null
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val phone_number: String? = null,
    val role: String? = null
)
