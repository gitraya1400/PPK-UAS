package com.example.banksoalstis.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.banksoalstis.api.RetrofitClient
import com.example.banksoalstis.databinding.ActivityAdminMainBinding
import com.example.banksoalstis.model.MataKuliahDto
import com.example.banksoalstis.ui.adapter.MataKuliahAdapter
import com.example.banksoalstis.ui.auth.LoginActivity
import com.example.banksoalstis.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: MataKuliahAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupRecyclerView()
        setupListeners()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = MataKuliahAdapter(emptyList()) { matkul ->
            Toast.makeText(this, "Klik: ${matkul.nama}", Toast.LENGTH_SHORT).show()
            // Nanti di sini kita arahkan ke Detail Pertemuan
        }
        binding.rvMatkul.layoutManager = LinearLayoutManager(this)
        binding.rvMatkul.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener { // Atau nama tombol icon profil Anda
            val intent = Intent(this, com.example.banksoalstis.ui.profile.ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.fabAdd.setOnClickListener {
            Toast.makeText(this, "Fitur Tambah Matkul (Next)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadData() {
        // Admin pakai endpoint getAllMataKuliah
        RetrofitClient.getInstance(this).getAllMataKuliah().enqueue(object : Callback<List<MataKuliahDto>> {
            override fun onResponse(call: Call<List<MataKuliahDto>>, response: Response<List<MataKuliahDto>>) {
                if (response.isSuccessful) {
                    response.body()?.let { adapter.updateData(it) }
                }
            }
            override fun onFailure(call: Call<List<MataKuliahDto>>, t: Throwable) {
                Toast.makeText(this@AdminMainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}