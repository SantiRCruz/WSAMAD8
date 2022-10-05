package com.example.wsamad8.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.recyclerview.widget.RecyclerView
import com.example.wsamad8.R
import com.example.wsamad8.data.models.DataHistory
import com.example.wsamad8.databinding.ActivityHomeBinding
import com.example.wsamad8.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat

class HistoryAdapter(private val list : List<DataHistory>):RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HistoryViewHolder(binding,parent.context)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding,private val context: Context):RecyclerView.ViewHolder(binding.root){
        fun bind(item: DataHistory){
            binding.txtMonthDay.text = SimpleDateFormat("MM/dd").format(SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(item.date))
            binding.txtYearHour.text = SimpleDateFormat("/yyyy KK:mma").format(SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(item.date))
            if (item.probability_infection>50){
                binding.txtTitle.text = "CALL TO DOCTOR"
                binding.llBg.backgroundTintList = getColorStateList(context, R.color.dark_blue)
            }else{
                binding.txtTitle.text = "CLEAR"
                binding.llBg.backgroundTintList = getColorStateList(context,R.color.blue_200)
            }
        }
    }
}