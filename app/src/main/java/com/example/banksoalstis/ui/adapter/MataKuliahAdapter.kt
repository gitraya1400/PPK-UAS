package com.example.banksoalstis.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.banksoalstis.databinding.ItemMatakuliahBinding
import com.example.banksoalstis.model.MataKuliahDto

class MataKuliahAdapter(
    private var list: List<MataKuliahDto>,
    private val onClick: (MataKuliahDto) -> Unit // Callback saat diklik
) : RecyclerView.Adapter<MataKuliahAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMatakuliahBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMatakuliahBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            tvKode.text = item.kode
            tvNamaMatkul.text = item.nama
            tvSks.text = "${item.sks} SKS"
            tvDeskripsi.text = item.deskripsi ?: "-"

            root.setOnClickListener { onClick(item) }
        }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<MataKuliahDto>) {
        list = newList
        notifyDataSetChanged()
    }
}