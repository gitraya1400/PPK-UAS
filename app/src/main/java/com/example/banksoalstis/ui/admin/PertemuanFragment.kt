package com.example.banksoalstis.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.banksoalstis.api.RetrofitClient
import com.example.banksoalstis.databinding.DialogFormPertemuanBinding
import com.example.banksoalstis.databinding.FragmentPertemuanBinding
import com.example.banksoalstis.model.PertemuanDto
import com.example.banksoalstis.ui.adapter.PertemuanAdapter
import com.example.banksoalstis.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PertemuanFragment : Fragment() {

    private var _binding: FragmentPertemuanBinding? = null
    private val binding get() = _binding!!
    private var matkulId: Long = -1L
    private lateinit var adapter: PertemuanAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { matkulId = it.getLong("MATKUL_ID") }
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPertemuanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadData()
        binding.fabAdd.setOnClickListener {
            showFormDialog(null) // Mode Tambah
        }

        // Asumsi: Di layout fragment_pertemuan.xml ada FloatingActionButton id: fabAdd
        // Jika belum ada, tambahkan di XML-nya
        /*
        binding.fabAdd.setOnClickListener {
            showFormDialog(null) // Mode Tambah
        }
        */
        // ATAU Jika Anda belum punya FAB, saya buatkan fungsi temporary di sini:
        // Panggil showFormDialog(null) jika ada trigger tombol tambah
    }

    private fun setupRecyclerView() {
        adapter = PertemuanAdapter(emptyList(),
            onEdit = { pertemuan -> showFormDialog(pertemuan) }, // Mode Edit
            onDelete = { pertemuan -> confirmDelete(pertemuan) } // Mode Hapus
        )
        binding.rvPertemuan.layoutManager = LinearLayoutManager(context)
        binding.rvPertemuan.adapter = adapter
    }

    private fun loadData() {
        val token = "Bearer ${sessionManager.getToken()}"
        RetrofitClient.getInstance(requireContext()).getPertemuanByMatkul(token, matkulId).enqueue(object : Callback<List<PertemuanDto>> {
            override fun onResponse(call: Call<List<PertemuanDto>>, response: Response<List<PertemuanDto>>) {
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    // Sort berdasarkan nomor pertemuan agar rapi
                    val sortedList = list.sortedBy { it.nomorPertemuan }
                    adapter.updateData(sortedList)
                }
            }
            override fun onFailure(call: Call<List<PertemuanDto>>, t: Throwable) {
                Toast.makeText(context, "Error load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- LOGIC DIALOG (Create & Edit) ---
    private fun showFormDialog(existingData: PertemuanDto?) {
        val dialogBinding = DialogFormPertemuanBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        // Setup Transparent Background agar CardView rounded terlihat
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Isi data jika Mode Edit
        if (existingData != null) {
            dialogBinding.tvHeaderDialog.text = "Edit Pertemuan"
            dialogBinding.etNomor.setText(existingData.nomorPertemuan.toString())
            dialogBinding.etJudul.setText(existingData.judul)
            dialogBinding.etDeskripsi.setText(existingData.deskripsi)
            dialogBinding.btnSimpan.text = "Update"
        }

        dialogBinding.btnBatal.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnSimpan.setOnClickListener {
            val nomor = dialogBinding.etNomor.text.toString().toIntOrNull()
            val judul = dialogBinding.etJudul.text.toString()
            val deskripsi = dialogBinding.etDeskripsi.text.toString()

            // VALIDASI INPUT
            if (nomor == null || judul.isEmpty()) {
                Toast.makeText(context, "Nomor dan Judul wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Buat Object Request
            // Perhatikan: ID dikirim 0 jika baru, atau ID asli jika edit
            val request = PertemuanDto(
                id = existingData?.id ?: 0,
                mataKuliahId = matkulId, // <--- TAMBAHKAN INI (Ambil dari variabel global fragment)
                nomorPertemuan = nomor,
                judul = judul,
                deskripsi = deskripsi
            )

            if (existingData == null) {
                doCreate(request, dialog)
            } else {
                doUpdate(existingData.id, request, dialog)
            }
        }

        dialog.show()
    }

    private fun doCreate(data: PertemuanDto, dialog: AlertDialog) {
        val token = "Bearer ${sessionManager.getToken()}"
        // Pastikan di DTO Pertemuan ada field 'mataKuliahId' jika Backend butuh,
        // ATAU Backend mengambil mataKuliahId dari URL/Query parameter.
        // Berdasarkan API Service Anda: createPertemuan(token, body)
        // Backend perlu tahu Matkul ID. Biasanya dimasukkan ke dalam body DTO atau URL.
        // Jika API Anda: POST /api/pertemuan?mataKuliahId=...
        // Maka kita perlu update logic di sini.

        // SEMENTARA: Asumsi backend logic createPertemuan menerima body yg berisi mkId
        // Update PertemuanDto.kt Anda tambahkan val mataKuliahId: Long? jika perlu.

        // JIKA API Create Anda: @POST("api/pertemuan") fun create(@Body p: PertemuanDto)
        // Maka Anda harus set mataKuliahId di objek 'data' sebelum dikirim.

        RetrofitClient.getInstance(requireContext()).createPertemuan(token, data).enqueue(object : Callback<PertemuanDto> {
            override fun onResponse(call: Call<PertemuanDto>, response: Response<PertemuanDto>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Berhasil dibuat!", Toast.LENGTH_SHORT).show()
                    loadData()
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Gagal: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<PertemuanDto>, t: Throwable) {
                Toast.makeText(context, "Error koneksi", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun doUpdate(id: Long, data: PertemuanDto, dialog: AlertDialog) {
        val token = "Bearer ${sessionManager.getToken()}"
        RetrofitClient.getInstance(requireContext()).updatePertemuan(token, id, data).enqueue(object : Callback<PertemuanDto> {
            override fun onResponse(call: Call<PertemuanDto>, response: Response<PertemuanDto>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Berhasil diupdate!", Toast.LENGTH_SHORT).show()
                    loadData()
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "Gagal update", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<PertemuanDto>, t: Throwable) {}
        })
    }

    private fun confirmDelete(data: PertemuanDto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Pertemuan?")
            .setMessage("Yakin hapus pertemuan ${data.nomorPertemuan}: ${data.judul}?")
            .setPositiveButton("Hapus") { _, _ ->
                val token = "Bearer ${sessionManager.getToken()}"
                RetrofitClient.getInstance(requireContext()).deletePertemuan(token, data.id).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Terhapus", Toast.LENGTH_SHORT).show()
                            loadData()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                })
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        fun newInstance(matkulId: Long) = PertemuanFragment().apply {
            arguments = Bundle().apply { putLong("MATKUL_ID", matkulId) }
        }
    }
}