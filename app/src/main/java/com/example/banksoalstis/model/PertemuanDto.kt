package com.example.banksoalstis.model

data class PertemuanDto(
    val id: Long = 0, // Default 0 biar aman buat Create
    val mataKuliahId: Long, // <--- INI WAJIB ADA
    val nomorPertemuan: Int,
    val judul: String,
    val deskripsi: String?
)