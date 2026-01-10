package com.example.banksoalstis.ui.dosen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.banksoalstis.api.RetrofitClient
import com.example.banksoalstis.databinding.ActivityDosenMainBinding
import com.example.banksoalstis.model.MataKuliahDto
import com.example.banksoalstis.ui.adapter.MataKuliahAdapter
import com.example.banksoalstis.ui.auth.LoginActivity
import com.example.banksoalstis.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DosenMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDosenMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: MataKuliahAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosenMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupUI()
        loadData()
    }

    private fun setupUI() {
        binding.tvWelcome.text = "Halo, ${sessionManager.getName()}"

        adapter = MataKuliahAdapter(emptyList()) { matkul ->
            // Kirim ID dan Nama Matkul ke halaman Pertemuan
            val intent = Intent(this, com.example.banksoalstis.ui.pertemuan.PertemuanActivity::class.java)
            intent.putExtra("MATKUL_ID", matkul.id)
            intent.putExtra("MATKUL_NAMA", matkul.nama)
            startActivity(intent)
        }

        binding.rvMatkulDosen.layoutManager = LinearLayoutManager(this)
        binding.rvMatkulDosen.adapter = adapter

        binding.btnLogoutDosen.setOnClickListener { // Atau nama tombol icon profil Anda
            val intent = Intent(this, com.example.banksoalstis.ui.profile.ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadData() {
        val userId = sessionManager.getUserId()
        // Panggil endpoint khusus Dosen
        RetrofitClient.getInstance(this).getMataKuliahByDosen(userId).enqueue(object : Callback<List<MataKuliahDto>> {
            override fun onResponse(call: Call<List<MataKuliahDto>>, response: Response<List<MataKuliahDto>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data.isNullOrEmpty()) {
                        Toast.makeText(this@DosenMainActivity, "Belum ada matkul yang ditugaskan", Toast.LENGTH_LONG).show()
                    } else {
                        adapter.updateData(data)
                    }
                }
            }
            override fun onFailure(call: Call<List<MataKuliahDto>>, t: Throwable) {
                Toast.makeText(this@DosenMainActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}