package com.example.banksoalstis.model

data class PilihanGandaDto(
    val id: Long?,
    val pertemuanId: Long,
    val pertanyaan: String,
    val tingkatKesulitan: String, // MUDAH, SEDANG, SULIT
    val pilihanJawaban: List<String>,
    val indexJawabanBenar: Int,
    val pembahasan: String?,
    val tahunPembuatan: Int,
    val semester: String,
    val status: String? = "DRAFT",
    val catatan: String? = null,
    val gambar: String? = null
)