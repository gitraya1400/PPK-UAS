package com.example.banksoalstis.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.banksoalstis.api.RetrofitClient
import com.example.banksoalstis.databinding.ActivityProfileBinding
import com.example.banksoalstis.model.ChangePasswordDto
import com.example.banksoalstis.model.UpdateProfileDto
import com.example.banksoalstis.model.UserDto
import com.example.banksoalstis.ui.auth.LoginActivity
import com.example.banksoalstis.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        loadProfile()
        setupListeners()
    }

    private fun loadProfile() {
        val token = "Bearer ${sessionManager.getToken()}" // FIX: Pakai Token

        RetrofitClient.getInstance(this).getProfile(token).enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        binding.etEmail.setText(it.email)
                        binding.etNama.setText(it.name)
                        binding.etNip.setText(it.nip ?: "")
                    }
                }
            }
            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Gagal load profil", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupListeners() {
        val token = "Bearer ${sessionManager.getToken()}" // FIX: Pakai Token untuk update juga

        // 1. Update Profil
        binding.btnUpdateProfile.setOnClickListener {
            val name = binding.etNama.text.toString()
            val nip = binding.etNip.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = UpdateProfileDto(name, nip)
            RetrofitClient.getInstance(this).updateProfile(token, request).enqueue(object : Callback<UserDto> {
                override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProfileActivity, "Profil berhasil diupdate!", Toast.LENGTH_SHORT).show()
                        // Update session lokal jika perlu (tergantung respons body)
                        response.body()?.let {
                            sessionManager.saveUserDetail(it.id, it.role, it.name)
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "Gagal update profil", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<UserDto>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Error koneksi", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // 2. Ganti Password
        binding.btnChangePassword.setOnClickListener {
            val oldPass = binding.etOldPassword.text.toString()
            val newPass = binding.etNewPassword.text.toString()

            if (oldPass.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Isi password lama dan baru", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = ChangePasswordDto(oldPass, newPass)

            // FIX: changePassword return Void, bukan ApiResponse
            RetrofitClient.getInstance(this).changePassword(token, request).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProfileActivity, "Password berhasil diganti", Toast.LENGTH_SHORT).show()
                        binding.etOldPassword.text?.clear()
                        binding.etNewPassword.text?.clear()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Gagal: Password lama salah?", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Error koneksi", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // 3. Logout
        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}