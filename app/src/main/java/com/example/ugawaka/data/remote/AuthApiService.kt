package com.example.ugawaka.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApiService {
    @GET("sanctum/csrf-cookie")
    suspend fun initializeCsrf(): Response<Unit>

    @POST("api/customer/login")
    suspend fun customerLogin(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/customer/register")
    suspend fun customerRegister(@Body request: RegisterRequest): Response<LoginResponse>

    @POST("api/provider/login")
    suspend fun providerLogin(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/provider/register")
    suspend fun providerRegister(@Body request: RegisterRequest): Response<LoginResponse>

    @GET("api/customer/services")
    suspend fun getServices(): Response<ServicesResponse>

    @GET("api/customer/services/{id}")
    suspend fun getServiceDetail(@Path("id") id: Int): Response<ServiceDetailResponse>

    @GET("api/customer/providers/{id}")
    suspend fun getProviderDetail(@Path("id") id: Int): Response<ProviderDetailResponse>

    @GET("api/provider/profile")
    suspend fun getMyProviderProfile(): Response<ProviderDetailResponse>

    @GET("api/provider/home")
    suspend fun getProviderHome(): Response<ProviderHomeResponse>

    @GET("api/provider/bookings")
    suspend fun getProviderBookings(): Response<BookingsListResponse>

    @POST("api/provider/bookings/{id}/accept")
    suspend fun acceptBooking(@Path("id") id: Int): Response<BookingResponse>

    @POST("api/provider/bookings/{id}/reject")
    suspend fun rejectBooking(@Path("id") id: Int): Response<BookingResponse>

    @POST("api/provider/bookings/{id}/start")
    suspend fun startBooking(@Path("id") id: Int): Response<BookingResponse>

    @POST("api/provider/bookings/{id}/complete")
    suspend fun completeBooking(@Path("id") id: Int): Response<BookingResponse>

    @POST("api/customer/bookings")
    suspend fun createBooking(@Body request: BookingCreateRequest): Response<BookingResponse>

    @GET("api/customer/bookings/{id}")
    suspend fun getBookingDetail(@Path("id") id: Int): Response<BookingResponse>

    @PUT("api/customer/bookings/{id}/cancel")
    suspend fun cancelBooking(@Path("id") id: Int): Response<BookingResponse>

    @GET("api/customer/bookings")
    suspend fun getBookings(): Response<BookingsListResponse>
}

data class ServicesResponse(
    val services: List<ServiceDto>
)

data class ServiceDetailResponse(
    val service: ServiceDto
)

data class ProviderDetailResponse(
    val provider: ProviderDto? = null,
    val profile: ProviderDto? = null
)

data class ProviderHomeResponse(
    val total_earnings: String,
    val balance: String,
    val completed_bookings: Int,
    val pending_bookings: Int,
    val rating: String,
    val recent_bookings: List<BookingDto>
)

data class BookingsListResponse(
    val bookings: List<BookingDto>
)

data class BookingCreateRequest(
    val provider_id: Int,
    val scheduled_on: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)

data class BookingResponse(
    val message: String? = null,
    val booking: BookingDto
)

data class BookingDto(
    val id: Int,
    val customer_id: Int,
    val provider_id: Int,
    val service_id: Int? = null,
    val scheduled_on: String,
    val description: String?,
    val status: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val location_name: String? = null,
    val provider: ProviderDto? = null,
    val service: ServiceDto? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

data class ServiceDto(
    val id: Int,
    val name: String,
    val description: String?,
    val icon: String?,
    val providers: List<ProviderDto>? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

data class ProviderDto(
    val id: Int,
    val name: String,
    val email: String,
    val phone_number: String?,
    val latitude: Double?,
    val longitude: Double?,
    val nin: String?,
    val rate: Any?,
    val status: String?,
    val about: String?,
    val total_earnings: Any?,
    val balance: Any?,
    val service_id: Int?,
    val service_name: String? = null,
    val created_at: String?,
    val updated_at: String?
)
