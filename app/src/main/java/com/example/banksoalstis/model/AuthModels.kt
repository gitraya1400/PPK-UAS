package com.example.banksoalstis.model

import com.google.gson.annotations.SerializedName

// Data yang dikirim saat Login
data class LoginRequest(
    val email: String,
    val password: String
)

// Data yang diterima setelah Login sukses
data class LoginResponse(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("tokenType")
    val tokenType: String,

    val id: Long,
    val username: String,
    val email: String,
    val roles: List<String>
)
data class UserRegistrationDto(
    val name: String,
    val email: String,
    val password: String,
    val nip: String?
)