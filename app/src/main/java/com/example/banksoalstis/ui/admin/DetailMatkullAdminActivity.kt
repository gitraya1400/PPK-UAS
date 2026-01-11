package com.example.banksoalstis.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.banksoalstis.api.RetrofitClient
import com.example.banksoalstis.databinding.ActivityDetailMatkulAdminBinding
import com.example.banksoalstis.model.MataKuliahDto
import com.example.banksoalstis.utils.SessionManager
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailMatkulAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMatkulAdminBinding
    private lateinit var sessionManager: SessionManager
    private var matkulId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMatkulAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        matkulId = intent.getLongExtra("MATKUL_ID", -1L)
        if (matkulId == -1L) {
            Toast.makeText(this, "Matkul ID Invalid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupHeaderData()
        setupViewPager()
        setContentView(binding.root)

        // Aktifkan tombol back
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupHeaderData() {
        val token = "Bearer ${sessionManager.getToken()}"
        RetrofitClient.getInstance(this).getMataKuliahDetail(token, matkulId).enqueue(object : Callback<MataKuliahDto> {
            override fun onResponse(call: Call<MataKuliahDto>, response: Response<MataKuliahDto>) {
                if (response.isSuccessful) {
                    val mk = response.body()
                    mk?.let {
                        binding.tvHeaderNamaMatkul.text = it.nama
                        binding.tvHeaderKodeSks.text = "${it.kode} • Semester ${it.semester} • ${it.sks} SKS"
                    }
                }
            }
            override fun onFailure(call: Call<MataKuliahDto>, t: Throwable) {
                Toast.makeText(this@DetailMatkulAdminActivity, "Gagal load header", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupViewPager() {
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                // FIX: Memanggil companion object newInstance()
                // Pastikan di PertemuanFragment.kt dan DosenPengampuFragment.kt
                // SUDAH ada blok 'companion object { fun newInstance(...) }'
                return when (position) {
                    0 -> PertemuanFragment.newInstance(matkulId)
                    else -> DosenPengampuFragment.newInstance(matkulId)
                }
            }
        }
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "Pertemuan" else "Dosen"
        }.attach()
    }
}