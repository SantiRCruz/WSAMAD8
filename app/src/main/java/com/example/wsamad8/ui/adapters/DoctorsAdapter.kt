package com.example.wsamad8.ui.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.wsamad8.R
import com.example.wsamad8.data.models.DataHistory
import com.example.wsamad8.data.models.Hits
import com.example.wsamad8.databinding.ItemDoctorsBinding
import com.example.wsamad8.databinding.ItemHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat

class DoctorsAdapter(private val list: List<Hits>) :
    RecyclerView.Adapter<DoctorsAdapter.DoctorsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorsViewHolder {
        val binding = ItemDoctorsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DoctorsViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: DoctorsViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class DoctorsViewHolder(
        private val binding: ItemDoctorsBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Hits) {
            val url = URL(item.largeImageURL).openConnection() as HttpURLConnection
            CoroutineScope(Dispatchers.IO).launch {
                val bitmap = BitmapFactory.decodeStream(url.inputStream)
                CoroutineScope(Dispatchers.Main).launch {
                    binding.img.setImageBitmap(bitmap)
                }
            }
        }
    }
}