package com.example.banksoalstis.ui.pertemuan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.banksoalstis.api.RetrofitClient
import com.example.banksoalstis.databinding.ActivityPertemuanBinding
import com.example.banksoalstis.model.MataKuliahDto
import com.example.banksoalstis.model.PertemuanDto
import com.example.banksoalstis.ui.adapter.PertemuanAdapter
import com.example.banksoalstis.ui.soal.SoalActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PertemuanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPertemuanBinding
    private lateinit var adapter: PertemuanAdapter
    private var mataKuliahId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPertemuanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil ID dari halaman sebelumnya
        mataKuliahId = intent.getLongExtra("MATKUL_ID", -1)
        val namaMatkul = intent.getStringExtra("MATKUL_NAMA") ?: "Detail Mata Kuliah"

        binding.tvTitle.text = namaMatkul
        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = PertemuanAdapter(emptyList()) { pertemuan ->
            // Klik Pertemuan -> Buka Soal
            val intent = Intent(this, SoalActivity::class.java)
            intent.putExtra("PERTEMUAN_ID", pertemuan.id)
            intent.putExtra("PERTEMUAN_JUDUL", pertemuan.judul)
            startActivity(intent)
        }
        binding.rvPertemuan.layoutManager = LinearLayoutManager(this)
        binding.rvPertemuan.adapter = adapter
    }

    private fun loadData() {
        RetrofitClient.getInstance(this).getMataKuliahDetail(mataKuliahId).enqueue(object : Callback<MataKuliahDto> {
            override fun onResponse(call: Call<MataKuliahDto>, response: Response<MataKuliahDto>) {
                if (response.isSuccessful) {
                    val matkul = response.body()
                    val listPertemuan = matkul?.pertemuanList
                    if (!listPertemuan.isNullOrEmpty()) {
                        // Sort biar urut (Pertemuan 1, 2, 3...)
                        val sortedList = listPertemuan.sortedBy { it.nomorPertemuan }
                        adapter.updateData(sortedList)
                    } else {
                        Toast.makeText(this@PertemuanActivity, "Belum ada pertemuan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<MataKuliahDto>, t: Throwable) {
                Toast.makeText(this@PertemuanActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}