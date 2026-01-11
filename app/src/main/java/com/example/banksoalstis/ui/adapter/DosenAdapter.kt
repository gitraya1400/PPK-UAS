package com.example.banksoalstis.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.banksoalstis.databinding.ItemPertemuanBinding
import com.example.banksoalstis.model.UserDto

class DosenAdapter(
    private var list: List<UserDto>,
    private val onClick: (UserDto) -> Unit,
    private val onDelete: ((UserDto) -> Unit)? = null
) : RecyclerView.Adapter<DosenAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPertemuanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPertemuanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            // PERBAIKAN DI SINI: Gunakan ID sesuai item_pertemuan.xml
            tvJudul.text = item.name                // Nama Dosen
            tvDeskripsi.text = "NIP: ${item.nip ?: "-"}"  // NIP

            // tvNomor kita isi teks statis atau nomor urut
            tvNomor.text = "Pengajar ${position + 1}"

            root.setOnClickListener { onClick(item) }
        }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<UserDto>) {
        list = newList
        notifyDataSetChanged()
    }
}