package com.example.wsamad8.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.wsamad8.R
import com.example.wsamad8.data.models.DataHistory
import com.example.wsamad8.data.models.Symptom
import com.example.wsamad8.databinding.ItemHistoryBinding
import com.example.wsamad8.databinding.ItemSymptomBinding
import java.text.SimpleDateFormat

class SymptomAdapter (private val list : List<Symptom>): RecyclerView.Adapter<SymptomAdapter.SymptomViewHolder>() {
    private val checkBox = mutableListOf<CheckBox>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomViewHolder {
        val binding = ItemSymptomBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SymptomViewHolder(binding,parent.context)
    }

    override fun onBindViewHolder(holder: SymptomViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class SymptomViewHolder(private val binding: ItemSymptomBinding, private val context: Context):
        RecyclerView.ViewHolder(binding.root){
        fun bind(item: Symptom){
            checkBox.add(binding.checkbox)
            binding.txt.text = item.title
        }
    }
    fun checked():List<Int>{
        val numList = mutableListOf<Int>()
        checkBox.forEachIndexed { index, checkBox ->
            if (checkBox.isChecked) numList.add(list[index].id)
        }
        return numList
    }
}