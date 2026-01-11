package com.example.banksoalstis.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("tokenType")
    val tokenType: String,

    // HAPUS id karena backend tidak mengirim id saat login
    // val id: Long,

    // GANTI 'username' jadi 'name' sesuai backend
    val name: String,

    val email: String,

    // PERBAIKAN UTAMA: Backend kirim 'role' (String tunggal), BUKAN List
    val role: String
)

data class UserRegistrationDto(
    val name: String,
    val email: String,
    val password: String,
    val nip: String?
)