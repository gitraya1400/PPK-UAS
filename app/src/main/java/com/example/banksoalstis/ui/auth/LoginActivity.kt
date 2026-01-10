package com.example.banksoalstis.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.banksoalstis.api.RetrofitClient
import com.example.banksoalstis.databinding.ActivityLoginBinding
import com.example.banksoalstis.model.LoginRequest
import com.example.banksoalstis.model.LoginResponse
import com.example.banksoalstis.ui.admin.AdminMainActivity
import com.example.banksoalstis.ui.dosen.DosenMainActivity
import com.example.banksoalstis.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Cek jika sudah login sebelumnya
        checkSession()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                doLogin(email, password)
            } else {
                Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun doLogin(email: String, pass: String) {
        val service = RetrofitClient.getInstance(this)
        val loginRequest = LoginRequest(email, pass)

        service.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // 1. Simpan Token & Data User
                        sessionManager.saveAuthToken(body.accessToken)
                        // Ambil role pertama (biasanya list, kita ambil index 0)
                        val role = body.roles.firstOrNull() ?: "DOSEN"
                        sessionManager.saveUserDetail(body.id, role, body.username)

                        // 2. Arahkan sesuai Role
                        navigateBasedOnRole(role)
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login Gagal: Cek Email/Pass", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun navigateBasedOnRole(role: String) {
        if (role == "ADMIN") {
            startActivity(Intent(this, AdminMainActivity::class.java))
        } else {
            startActivity(Intent(this, DosenMainActivity::class.java))
        }
        finish() // Tutup Login Activity agar tidak bisa di-back
    }

    private fun checkSession() {
        if (sessionManager.getToken() != null) {
            val role = sessionManager.getRole() ?: "DOSEN"
            navigateBasedOnRole(role)
        }
    }
}