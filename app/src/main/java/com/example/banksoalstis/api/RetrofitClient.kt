package com.example.banksoalstis.api

import android.content.Context
import com.example.banksoalstis.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Ganti 10.0.2.2 jika pakai Emulator.
    // Jika pakai HP fisik, ganti dengan IP Laptop (misal: 192.168.1.5)
    private const val BASE_URL = "http://10.0.2.2:8080/"

    fun getInstance(context: Context): ApiService {
        val sessionManager = SessionManager(context)

        val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        // Otomatis tempel Token JWT di Header
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                val token = sessionManager.getToken()
                if (!token.isNullOrEmpty()) {
                    request.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(request.build())
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}