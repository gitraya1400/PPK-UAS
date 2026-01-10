package com.example.banksoalstis.api

import com.example.banksoalstis.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // --- AUTH ---
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // --- USER ---
    @GET("api/user/profile")
    fun getProfile(): Call<UserDto>

    // --- MATA KULIAH (Admin & Dosen) ---
    @GET("api/matakuliah") // Admin lihat semua
    fun getAllMataKuliah(): Call<List<MataKuliahDto>>

    @GET("api/matakuliah/dosen/{dosenId}") // Dosen lihat yg diajar
    fun getMataKuliahByDosen(@Path("dosenId") dosenId: Long): Call<List<MataKuliahDto>>

    // --- SOAL ---
    @GET("api/soal/search")
    fun searchSoal(
        @Query("mataKuliahId") matkulId: Long?,
        @Query("tipeSoal") tipe: String?
    ): Call<List<SoalDto>>
    // --- MATA KULIAH DETAIL (Untuk dapat List Pertemuan) ---
    @GET("api/matakuliah/{id}")
    fun getMataKuliahDetail(@Path("id") id: Long): Call<MataKuliahDto>

    // --- SOAL (Update searchSoal agar parameternya jelas) ---
    @GET("api/soal/search")
    fun getSoalByPertemuan(
        @Query("pertemuanId") pertemuanId: Long
    ): Call<List<SoalDto>>
    @PUT("api/user/profile")
    fun updateProfile(@Body request: UpdateProfileDto): Call<UserDto>

    @PUT("api/user/change-password")
    fun changePassword(@Body request: ChangePasswordDto): Call<ApiResponse> // Pastikan backend return JSON

    @POST("api/auth/register")
    fun register(@Body request: UserRegistrationDto): Call<UserDto>
}