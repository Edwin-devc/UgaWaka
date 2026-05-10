package com.example.ugawaka.data.remote

import android.content.Context
import com.example.ugawaka.data.local.SessionManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLDecoder

object RetrofitClient {
    private const val BASE_URL = "https://ugawaka.evote.tech/" // Default for Android Emulator to localhost

    var authToken: String? = null
    var userName: String = "User"

    fun init(context: Context) {
        val sessionManager = SessionManager.getInstance(context)
        authToken = sessionManager.fetchAuthToken()
        userName = sessionManager.fetchUserName() ?: "User"
    }

    private val cookieJar = object : CookieJar {
        private val cookieStore = mutableMapOf<String, List<Cookie>>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url.host] = cookies
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: listOf()
        }
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            
            // Add Authorization header if token exists (will be set by AuthViewModel/SessionManager)
            // Note: In a real app, you might want to inject SessionManager here
            // For simplicity, we can use a static variable or a listener
            authToken?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }

            // If we have an XSRF-TOKEN cookie, add it as a header (required by many frameworks like Laravel)
            val cookies = cookieJar.loadForRequest(chain.request().url)
            val xsrfToken = cookies.find { it.name == "XSRF-TOKEN" }?.value
            if (xsrfToken != null) {
                val decodedToken = try {
                    URLDecoder.decode(xsrfToken, "UTF-8")
                } catch (e: Exception) {
                    xsrfToken
                }
                requestBuilder.addHeader("X-XSRF-TOKEN", decodedToken)
            }
            
            requestBuilder.addHeader("Accept", "application/json")
            requestBuilder.addHeader("X-Requested-With", "XMLHttpRequest")
            
            chain.proceed(requestBuilder.build())
        }
        .addInterceptor(logging)
        .build()

    val authService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(AuthApiService::class.java)
    }
}
