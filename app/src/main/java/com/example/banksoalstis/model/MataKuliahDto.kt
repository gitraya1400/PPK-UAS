package com.example.banksoalstis.model

import com.google.gson.annotations.SerializedName

data class MataKuliahDto(
    val id: Long,
    val kode: String,
    val nama: String,
    val semester: Int,
    val sks: Int,
    val deskripsi: String?,
    // Tambahan untuk Tahap 3
    @SerializedName("pertemuanList")
    val pertemuanList: List<PertemuanDto>? = null
)