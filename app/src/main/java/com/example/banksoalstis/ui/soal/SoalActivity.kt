package com.example.banksoalstis.ui.soal

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.banksoalstis.api.RetrofitClient
import com.example.banksoalstis.databinding.ActivitySoalBinding
import com.example.banksoalstis.model.SoalDto
import com.example.banksoalstis.ui.adapter.SoalAdapter
import com.example.banksoalstis.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySoalBinding
    private lateinit var adapter: SoalAdapter
    private lateinit var sessionManager: SessionManager // Tambah SessionManager
    private var pertemuanId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this) // Init Session

        pertemuanId = intent.getLongExtra("PERTEMUAN_ID", -1)
        val judulPertemuan = intent.getStringExtra("PERTEMUAN_JUDUL") ?: "Daftar Soal"

        binding.tvTitleSoal.text = judulPertemuan
        binding.btnBackSoal.setOnClickListener { finish() }

        setupRecyclerView()
        loadSoal()
    }

    private fun setupRecyclerView() {
        adapter = SoalAdapter(emptyList())
        binding.rvSoal.layoutManager = LinearLayoutManager(this)
        binding.rvSoal.adapter = adapter
    }

    private fun loadSoal() {
        val token = "Bearer ${sessionManager.getToken()}" // FIX: Pakai Token

        RetrofitClient.getInstance(this).getSoalByPertemuan(token, pertemuanId).enqueue(object : Callback<List<SoalDto>> {
            override fun onResponse(call: Call<List<SoalDto>>, response: Response<List<SoalDto>>) {
                if (response.isSuccessful) {
                    val listSoal = response.body()
                    if (!listSoal.isNullOrEmpty()) {
                        adapter.updateData(listSoal)
                    } else {
                        Toast.makeText(this@SoalActivity, "Belum ada soal", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<List<SoalDto>>, t: Throwable) {
                Toast.makeText(this@SoalActivity, "Gagal load soal", Toast.LENGTH_SHORT).show()
            }
        })
    }
}