package com.example.banksoalstis.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.banksoalstis.api.RetrofitClient
import com.example.banksoalstis.databinding.ActivityRegisterBinding
import com.example.banksoalstis.model.UserDto
import com.example.banksoalstis.model.UserRegistrationDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val nama = binding.etRegNama.text.toString()
            val email = binding.etRegEmail.text.toString()
            val nip = binding.etRegNip.text.toString()
            val password = binding.etRegPassword.text.toString()

            if (nama.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = UserRegistrationDto(nama, email, password, nip)
            doRegister(request)
        }

        binding.tvToLogin.setOnClickListener {
            finish() // Kembali ke Login
        }
    }

    private fun doRegister(request: UserRegistrationDto) {
        RetrofitClient.getInstance(this).register(request).enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registrasi Berhasil! Silakan Login", Toast.LENGTH_LONG).show()
                    finish() // Tutup halaman register, user login manual
                } else {
                    Toast.makeText(this@RegisterActivity, "Gagal: Email/NIP mungkin sudah dipakai", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}