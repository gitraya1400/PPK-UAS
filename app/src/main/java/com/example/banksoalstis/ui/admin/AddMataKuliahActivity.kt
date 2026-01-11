package com.example.banksoalstis.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.banksoalstis.api.RetrofitClient
import com.example.banksoalstis.databinding.ActivityAddMataKuliahBinding
import com.example.banksoalstis.model.MataKuliahDto
import com.example.banksoalstis.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMataKuliahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMataKuliahBinding
    private lateinit var sessionManager: SessionManager
    private var isEditMode = false
    private var matkulId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMataKuliahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Cek mode edit
        val matkul = intent.getSerializableExtra("EXTRA_MATKUL") as? MataKuliahDto
        if (matkul != null) {
            isEditMode = true
            matkulId = matkul.id
            setupEditMode(matkul)
        }

        binding.btnSimpan.setOnClickListener {
            saveData()
        }
    }

    private fun setupEditMode(matkul: MataKuliahDto) {
        binding.tvTitle.text = "Edit Mata Kuliah"
        binding.etKode.setText(matkul.kode)
        binding.etNama.setText(matkul.nama)
        binding.etSemester.setText(matkul.semester.toString())
        binding.etSks.setText(matkul.sks.toString())
        binding.etDeskripsi.setText(matkul.deskripsi)
        binding.btnSimpan.text = "UPDATE DATA"
    }

    private fun saveData() {
        val kode = binding.etKode.text.toString()
        val nama = binding.etNama.text.toString()
        val semStr = binding.etSemester.text.toString()
        val sksStr = binding.etSks.text.toString()
        val deskripsi = binding.etDeskripsi.text.toString()

        if (kode.isEmpty() || nama.isEmpty() || semStr.isEmpty() || sksStr.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi data wajib", Toast.LENGTH_SHORT).show()
            return
        }

        // FIX:
        // 1. id diisi 'matkulId' jika edit, atau '0L' jika create (backend akan generate sendiri)
        // 2. Hapus parameter 'pengajarList' karena sudah dihapus di DTO kamu
        val matkulDto = MataKuliahDto(
            id = matkulId ?: 0L,
            kode = kode,
            nama = nama,
            semester = semStr.toInt(),
            sks = sksStr.toInt(),
            deskripsi = deskripsi
            // pertemuanList default null, jadi tidak perlu ditulis
        )

        val token = "Bearer ${sessionManager.getToken()}"
        val service = RetrofitClient.getInstance(this)

        if (isEditMode && matkulId != null) {
            service.updateMataKuliah(token, matkulId!!, matkulDto).enqueue(callback)
        } else {
            service.createMataKuliah(token, matkulDto).enqueue(callback)
        }
    }

    private val callback = object : Callback<MataKuliahDto> {
        override fun onResponse(call: Call<MataKuliahDto>, response: Response<MataKuliahDto>) {
            if (response.isSuccessful) {
                Toast.makeText(this@AddMataKuliahActivity, "Berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@AddMataKuliahActivity, "Gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<MataKuliahDto>, t: Throwable) {
            Toast.makeText(this@AddMataKuliahActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
        }
    }
}