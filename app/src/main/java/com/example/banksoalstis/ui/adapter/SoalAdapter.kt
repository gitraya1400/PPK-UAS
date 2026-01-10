package com.example.banksoalstis.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.banksoalstis.databinding.ItemSoalBinding
import com.example.banksoalstis.model.SoalDto

class SoalAdapter(private var list: List<SoalDto>) : RecyclerView.Adapter<SoalAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemSoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            tvPertanyaan.text = "${position + 1}. ${item.pertanyaan}"

            // Badge Tipe Soal
            tvTipe.text = item.tipeSoal // PILIHAN_GANDA / ESAI
            if (item.tipeSoal == "PILIHAN_GANDA") {
                tvTipe.setBackgroundColor(0xFFE3F2FD.toInt()) // Biru muda
                tvTipe.setTextColor(0xFF0066CC.toInt())
            } else {
                tvTipe.setBackgroundColor(0xFFFBE9E7.toInt()) // Merah muda
                tvTipe.setTextColor(0xFFD84315.toInt())
            }

            tvKesulitan.text = item.tingkatKesulitan
        }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<SoalDto>) {
        list = newList
        notifyDataSetChanged()
    }
}