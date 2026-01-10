package com.example.banksoalstis.model

data class UpdateProfileDto(
    val name: String,
    val nip: String?
)

data class ChangePasswordDto(
    val oldPassword: String,
    val newPassword: String
)

// Response sederhana (opsional, jika backend kirim message string biasa)
data class ApiResponse(
    val message: String
)