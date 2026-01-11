package com.example.banksoalstis.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.banksoalstis.R
import com.example.banksoalstis.api.RetrofitClient
import com.example.banksoalstis.databinding.ActivityAdminMainBinding
import com.example.banksoalstis.model.MataKuliahDto
import com.example.banksoalstis.ui.adapter.MataKuliahAdapter
import com.example.banksoalstis.ui.auth.LoginActivity
import com.example.banksoalstis.ui.profile.ProfileActivity
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

        // Set Navigasi Bawah terpilih di 'Home'
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        setupRecyclerView()
        setupListeners()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        // Pastikan Home tetap terpilih saat kembali dari activity lain
        binding.bottomNavigation.menu.findItem(R.id.nav_home).isChecked = true
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = MataKuliahAdapter(emptyList()) { matkul ->
            val intent = Intent(this, DetailMatkulAdminActivity::class.java)
            intent.putExtra("MATKUL_ID", matkul.id ?: -1L)
            startActivity(intent)
        }
        binding.rvMatkul.layoutManager = LinearLayoutManager(this)
        binding.rvMatkul.adapter = adapter
    }

    private fun setupListeners() {
        // LOGIC BARU: Klik Ikon Logout di Toolbar Atas (Gantikan btnLogout lama)
        binding.btnToolbarLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Logic FAB (Tetap sama)
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddMataKuliahActivity::class.java))
        }

        // LOGIC BARU: Navigasi Bawah
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Sudah di halaman Home, tidak perlu ngapa-ngapain
                    // Atau bisa scroll ke atas: binding.rvMatkul.smoothScrollToPosition(0)
                    true
                }
                R.id.nav_profile -> {
                    // Pindah ke Activity Profile
                    startActivity(Intent(this, ProfileActivity::class.java))
                    // Return false agar ikon Profile tidak 'stuck' jadi biru di halaman ini
                    // (Karena kita pindah Activity, bukan ganti Fragment)
                    false
                }
                else -> false
            }
        }
    }

    private fun loadData() {
        val token = sessionManager.getToken()
        if (token == null) {
            Toast.makeText(this, "Sesi habis, silakan login ulang", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        RetrofitClient.getInstance(this).getAllMataKuliah("Bearer $token").enqueue(object : Callback<List<MataKuliahDto>> {
            override fun onResponse(call: Call<List<MataKuliahDto>>, response: Response<List<MataKuliahDto>>) {
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    adapter.updateData(list)
                } else {
                    Toast.makeText(this@AdminMainActivity, "Gagal memuat data: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<MataKuliahDto>>, t: Throwable) {
                Toast.makeText(this@AdminMainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}