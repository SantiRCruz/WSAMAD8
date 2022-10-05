package com.example.wsamad8.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wsamad8.R
import com.example.wsamad8.core.Constants
import com.example.wsamad8.data.get
import com.example.wsamad8.data.models.Symptom
import com.example.wsamad8.databinding.FragmentCheckInBinding
import com.example.wsamad8.databinding.ItemHistoryBinding
import com.example.wsamad8.ui.adapters.SymptomAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CheckInFragment : Fragment(R.layout.fragment_check_in) {
    private lateinit var binding: FragmentCheckInBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCheckInBinding.bind(view)


        setCheckList()
        setDate()
        clicks()

    }

    private fun setCheckList() {
        Constants.OKHTTP.newCall(get("symptom_list")).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
                Snackbar.make(binding.root, "Server Error!", Snackbar.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                val list = json.getJSONArray("data")
                val gson = Gson().fromJson<List<Symptom>>(list.toString(), object : TypeToken<List<Symptom>>() {}.type)
                requireActivity().runOnUiThread {
                    binding.rvList.adapter = SymptomAdapter(gson)
                    binding.rvList.layoutManager = LinearLayoutManager(requireContext())
                }
            }
        })
    }

    private fun clicks() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setDate() {
        if (Constants.actualDate == null) {
            binding.txtActualDate.text = SimpleDateFormat("MMM dd, yyyy").format(Date())
        } else {
            binding.txtActualDate.text = Constants.actualDate
        }
    }

}