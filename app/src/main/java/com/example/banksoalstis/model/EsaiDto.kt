package com.example.banksoalstis.model

data class EsaiDto(
    val id: Long?,
    val pertemuanId: Long,
    val pertanyaan: String,
    val tingkatKesulitan: String,
    val tahunPembuatan: Int,
    val semester: String,
    val status: String? = "DRAFT",
    val catatan: String? = null,
    val gambar: String? = null,
    val poinPenilaian: Int,
    val rubrik: String?,
    val jawabanKunci: String?
)