// File: com/example/banksoalstis/api/ApiService.kt
package com.example.banksoalstis.api

import com.example.banksoalstis.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // --- AUTH ---
    // WAJIB ADA 'api/' di depan
    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(@Body request: UserRegistrationDto): Call<Void> // Ubah ke Void jika backend tidak return body user

    // --- USER ---
    @GET("api/users/me") // Sesuaikan endpoint profile backend Anda (biasanya /api/users/me atau /api/user/profile)
    fun getProfile(@Header("Authorization") token: String): Call<UserDto>

    @PUT("api/users/me")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileDto
    ): Call<UserDto>

    @POST("api/users/change-password")
    fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordDto
    ): Call<Void>

    // --- MATA KULIAH ---
    @GET("api/matakuliah")
    fun getAllMataKuliah(@Header("Authorization") token: String): Call<List<MataKuliahDto>>

    // ALIAS untuk Dosen
    @GET("api/matakuliah")
    fun getMataKuliahByDosen(@Header("Authorization") token: String): Call<List<MataKuliahDto>>

    @POST("api/matakuliah")
    fun createMataKuliah(
        @Header("Authorization") token: String,
        @Body matkul: MataKuliahDto
    ): Call<MataKuliahDto>

    @PUT("api/matakuliah/{id}")
    fun updateMataKuliah(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body matkul: MataKuliahDto
    ): Call<MataKuliahDto>

    // Endpoint Detail
    @GET("api/matakuliah/{id}")
    fun getMataKuliahDetail(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Call<MataKuliahDto>

    @DELETE("api/matakuliah/{id}")
    fun deleteMataKuliah(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Call<Void>

    // --- PERTEMUAN ---

    // PERBAIKAN: Gunakan @Path, bukan @Query, dan tambahkan /matakuliah/ di URL
    // Agar cocok dengan Controller: @GetMapping("/matakuliah/{mataKuliahId}")
    @GET("api/pertemuan/matakuliah/{id}")
    fun getPertemuanByMatkul(
        @Header("Authorization") token: String,
        @Path("id") mataKuliahId: Long  // Ganti @Query jadi @Path
    ): Call<List<PertemuanDto>>

    // Create Pertemuan (Pastikan DTO mengirim mataKuliahId)
    @POST("api/pertemuan")
    fun createPertemuan(
        @Header("Authorization") token: String,
        @Body pertemuan: PertemuanDto
    ): Call<PertemuanDto>

    @PUT("api/pertemuan/{id}")
    fun updatePertemuan(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body pertemuan: PertemuanDto
    ): Call<PertemuanDto>

    @DELETE("api/pertemuan/{id}")
    fun deletePertemuan(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Call<Void>

    // --- SOAL ---
    @GET("api/soal/search")
    fun searchSoal(
        @Header("Authorization") token: String,
        @Query("mataKuliahId") mkId: Long?,
        @Query("pertemuanId") pertId: Long?
    ): Call<List<SoalDto>>

    // Alias untuk SoalActivity
    @GET("api/soal/search")
    fun getSoalByPertemuan(
        @Header("Authorization") token: String,
        @Query("pertemuanId") pertemuanId: Long
    ): Call<List<SoalDto>>

    @POST("api/soal/pilihanganda")
    fun createSoalPg(
        @Header("Authorization") token: String,
        @Body soal: PilihanGandaDto
    ): Call<PilihanGandaDto>

    @POST("api/soal/esai")
    fun createSoalEsai(
        @Header("Authorization") token: String,
        @Body soal: EsaiDto
    ): Call<EsaiDto>

    @Multipart
    @POST("api/soal/{id}/gambar")
    fun uploadGambar(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): Call<Void>

    @POST("api/soal/cetak-pdf")
    @Headers("Content-Type: application/json")
    fun cetakPdf(
        @Header("Authorization") token: String,
        @Body request: CetakSoalDto
    ): Call<ResponseBody>
    // 1. Ambil daftar semua user yang role-nya DOSEN
    @GET("api/user/dosen")
    fun getAllDosen(
        @Header("Authorization") token: String
    ): Call<List<UserDto>>

    // 2. Tugaskan Dosen ke Mata Kuliah
    @POST("api/matakuliah/{mkId}/dosen/{dosenId}")
    fun assignDosen(
        @Header("Authorization") token: String,
        @Path("mkId") mkId: Long,
        @Path("dosenId") dosenId: Long
    ): Call<Void>

    // 3. Hapus Dosen dari Mata Kuliah
    @DELETE("api/matakuliah/{mkId}/dosen/{dosenId}")
    fun removeDosen(
        @Header("Authorization") token: String,
        @Path("mkId") mkId: Long,
        @Path("dosenId") dosenId: Long
    ): Call<Void>
}