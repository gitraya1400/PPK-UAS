package com.example.banksoalstis.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.banksoalstis.databinding.ItemDosenSelectionBinding
import com.example.banksoalstis.model.UserDto

class AssignDosenAdapter(
    private var originalList: List<UserDto>, // Data Asli
    private val onAddClick: (UserDto) -> Unit
) : RecyclerView.Adapter<AssignDosenAdapter.ViewHolder>() {

    private var filteredList: MutableList<UserDto> = originalList.toMutableList()

    class ViewHolder(val binding: ItemDosenSelectionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDosenSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = filteredList[position]
        with(holder.binding) {
            tvNama.text = item.name
            tvNip.text = "NIP: ${item.nip ?: "-"}"

            btnAdd.setOnClickListener { onAddClick(item) }
        }
    }

    override fun getItemCount() = filteredList.size

    // LOGIKA FILTER (PENCARIAN)
    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(originalList)
        } else {
            val lowerCaseQuery = query.lowercase()
            for (item in originalList) {
                if (item.name.lowercase().contains(lowerCaseQuery) ||
                    (item.nip?.contains(lowerCaseQuery) == true)) {
                    filteredList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun updateData(newList: List<UserDto>) {
        originalList = newList
        filter("") // Reset filter saat data baru masuk
    }
}