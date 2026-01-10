package com.example.banksoalstis.model

data class SoalDto(
    val id: Long,
    val pertanyaan: String,
    val tipeSoal: String, // "PILIHAN_GANDA" atau "ESAI"
    val tingkatKesulitan: String,
    val gambar: String?, // Bisa null jika tidak ada gambar
    val pertemuanId: Long,
    val namaPertemuan: String?,
    val mataKuliahId: Long,
    val namaMataKuliah: String?
)