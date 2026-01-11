package com.example.banksoalstis.ui.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.banksoalstis.api.RetrofitClient
import com.example.banksoalstis.databinding.DialogAssignDosenBinding
import com.example.banksoalstis.databinding.FragmentDosenPengampuBinding
import com.example.banksoalstis.model.MataKuliahDto
import com.example.banksoalstis.model.UserDto
import com.example.banksoalstis.ui.adapter.AssignDosenAdapter
import com.example.banksoalstis.ui.adapter.DosenAdapter
import com.example.banksoalstis.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DosenPengampuFragment : Fragment() {

    private var _binding: FragmentDosenPengampuBinding? = null
    private val binding get() = _binding!!
    private var matkulId: Long = -1L

    // Adapter untuk list utama (yang sudah dia-ssign)
    private lateinit var adapter: DosenAdapter
    private lateinit var sessionManager: SessionManager

    // Simpan list pengajar saat ini untuk keperluan filter nanti
    private var currentPengajarList: List<UserDto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { matkulId = it.getLong("MATKUL_ID") }
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDosenPengampuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadDosenPengampu()

        binding.btnAssign.setOnClickListener {
            showAssignDialog()
        }
    }

    private fun setupRecyclerView() {
        adapter = DosenAdapter(emptyList(),
            onClick = { /* Opsional: Lihat profil dosen */ },
            onDelete = { dosen ->
                confirmRemoveDosen(dosen)
            }
        )
        binding.rvDosen.layoutManager = LinearLayoutManager(context)
        binding.rvDosen.adapter = adapter
    }

    // 1. Load Dosen yang SUDAH Assign
    private fun loadDosenPengampu() {
        val token = "Bearer ${sessionManager.getToken()}"
        RetrofitClient.getInstance(requireContext()).getMataKuliahDetail(token, matkulId).enqueue(object : Callback<MataKuliahDto> {
            override fun onResponse(call: Call<MataKuliahDto>, response: Response<MataKuliahDto>) {
                if (response.isSuccessful) {
                    val matkul = response.body()
                    val pengajarList = matkul?.pengajarList ?: emptyList()

                    // Update data ke adapter & simpan ke variabel global
                    currentPengajarList = pengajarList
                    adapter.updateData(pengajarList)
                }
            }
            override fun onFailure(call: Call<MataKuliahDto>, t: Throwable) {
                Toast.makeText(context, "Gagal load pengajar", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 2. Load SEMUA Dosen -> Lalu Tampilkan Dialog Custom
    private fun showAssignDialog() {
        val token = "Bearer ${sessionManager.getToken()}"

        // Tampilkan loading jika perlu
        RetrofitClient.getInstance(requireContext()).getAllDosen(token).enqueue(object : Callback<List<UserDto>> {
            override fun onResponse(call: Call<List<UserDto>>, response: Response<List<UserDto>>) {
                if (response.isSuccessful) {
                    val allDosen = response.body() ?: emptyList()

                    // FILTER LOGIC:
                    // Hanya tampilkan dosen yang BELUM ada di list pengajar saat ini
                    val availableDosen = allDosen.filter { dosenCandidate ->
                        currentPengajarList.none { existing -> existing.id == dosenCandidate.id }
                    }

                    if (availableDosen.isEmpty()) {
                        Toast.makeText(context, "Semua dosen sudah ditambahkan!", Toast.LENGTH_SHORT).show()
                    } else {
                        showCustomDialog(availableDosen)
                    }
                }
            }
            override fun onFailure(call: Call<List<UserDto>>, t: Throwable) {
                Toast.makeText(context, "Gagal ambil data dosen", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 3. Menampilkan Dialog Custom dengan Search Bar
    private fun showCustomDialog(dosenList: List<UserDto>) {
        // Inflate layout dialog custom
        val dialogBinding = DialogAssignDosenBinding.inflate(layoutInflater)

        // Setup Adapter untuk list di dalam dialog
        val selectionAdapter = AssignDosenAdapter(dosenList) { selectedDosen ->
            // Aksi saat tombol (+) diklik
            assignDosen(selectedDosen.id)
            // Opsional: Tutup dialog otomatis setelah memilih,
            // atau biarkan terbuka jika ingin assign banyak dosen sekaligus.
            // dialog.dismiss()
        }

        dialogBinding.rvDosenSelection.layoutManager = LinearLayoutManager(requireContext())
        dialogBinding.rvDosenSelection.adapter = selectionAdapter

        // Buat Dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        // Set background transparan agar CardView rounded terlihat bagus
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Logic Tombol Close (X)
        dialogBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        // Logic Search Bar (Filter Real-time)
        dialogBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selectionAdapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        dialog.show()
    }

    // 4. Eksekusi Assign ke Server
    private fun assignDosen(dosenId: Long) {
        val token = "Bearer ${sessionManager.getToken()}"
        RetrofitClient.getInstance(requireContext()).assignDosen(token, matkulId, dosenId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Dosen berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    loadDosenPengampu() // Refresh list utama di fragment
                    // Kita tidak perlu menutup dialog agar user bisa nambah dosen lain lagi kalau mau
                    // Kecuali jika Anda ingin dialog tertutup, tambahkan parameter dialog ke fungsi ini.
                } else {
                    Toast.makeText(context, "Gagal menambahkan: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error koneksi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 5. Hapus Dosen
    private fun confirmRemoveDosen(dosen: UserDto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Pengajar")
            .setMessage("Yakin hapus ${dosen.name} dari mata kuliah ini?")
            .setPositiveButton("Hapus") { _, _ ->
                val token = "Bearer ${sessionManager.getToken()}"
                RetrofitClient.getInstance(requireContext()).removeDosen(token, matkulId, dosen.id).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Dosen dihapus", Toast.LENGTH_SHORT).show()
                            loadDosenPengampu()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                })
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    companion object {
        fun newInstance(matkulId: Long) = DosenPengampuFragment().apply {
            arguments = Bundle().apply { putLong("MATKUL_ID", matkulId) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}