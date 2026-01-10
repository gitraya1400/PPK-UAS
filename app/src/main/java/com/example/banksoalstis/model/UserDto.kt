package com.example.banksoalstis.model

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val role: String, // "ADMIN" atau "DOSEN"
    val nip: String?
)