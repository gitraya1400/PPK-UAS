package com.example.banksoalstis.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.banksoalstis.databinding.ItemPertemuanBinding
import com.example.banksoalstis.model.PertemuanDto

class PertemuanAdapter(
    private var list: List<PertemuanDto>,
    private val onClick: (PertemuanDto) -> Unit
) : RecyclerView.Adapter<PertemuanAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPertemuanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPertemuanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            tvNomor.text = "Pertemuan ${item.nomorPertemuan}"
            tvJudul.text = item.judul
            tvDeskripsi.text = item.deskripsi ?: "-"
            root.setOnClickListener { onClick(item) }
        }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<PertemuanDto>) {
        list = newList
        notifyDataSetChanged()
    }
}