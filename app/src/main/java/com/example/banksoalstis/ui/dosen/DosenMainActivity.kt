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
import com.example.banksoalstis.ui.admin.DetailMatkulAdminActivity // FIX: Pakai Activity yang baru (Tab Layout)
import com.example.banksoalstis.ui.profile.ProfileActivity
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
            // FIX 1: Arahkan ke DetailMatkulAdminActivity (Halaman Tab Pertemuan & Dosen)
            // PertemuanActivity sudah dihapus, jadi jangan dipanggil lagi.
            val intent = Intent(this, DetailMatkulAdminActivity::class.java)

            // Kirim ID Matkul (Pastikan dikirim sebagai Long, handle null dengan -1L)
            intent.putExtra("MATKUL_ID", matkul.id ?: -1L)

            startActivity(intent)
        }

        binding.rvMatkulDosen.layoutManager = LinearLayoutManager(this)
        binding.rvMatkulDosen.adapter = adapter

        binding.btnLogoutDosen.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadData() {
        // FIX 2: Endpoint getMataKuliahByDosen butuh TOKEN (String), bukan UserId (Long)
        val token = "Bearer ${sessionManager.getToken()}"

        RetrofitClient.getInstance(this).getMataKuliahByDosen(token).enqueue(object : Callback<List<MataKuliahDto>> {
            override fun onResponse(call: Call<List<MataKuliahDto>>, response: Response<List<MataKuliahDto>>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data.isNullOrEmpty()) {
                        Toast.makeText(this@DosenMainActivity, "Belum ada matkul yang ditugaskan", Toast.LENGTH_LONG).show()
                        adapter.updateData(emptyList())
                    } else {
                        adapter.updateData(data)
                    }
                } else {
                    Toast.makeText(this@DosenMainActivity, "Gagal memuat: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<MataKuliahDto>>, t: Throwable) {
                Toast.makeText(this@DosenMainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}