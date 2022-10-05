package com.example.wsamad8.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.wsamad8.R
import com.example.wsamad8.core.Constants
import com.example.wsamad8.data.Data
import com.example.wsamad8.data.get
import com.example.wsamad8.data.getPix
import com.example.wsamad8.data.models.DataHistory
import com.example.wsamad8.data.models.HistoryList
import com.example.wsamad8.data.models.Hits
import com.example.wsamad8.databinding.FragmentHomeBinding
import com.example.wsamad8.ui.adapters.DoctorsAdapter
import com.example.wsamad8.ui.adapters.HistoryAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        setDate()
        obtainCases()
        obtainHistory()
        obtainDoctors()
        clicks()
    }

    private fun clicks() {
        binding.btnCheck.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_checkInFragment) }
    }

    private fun obtainDoctors() {
        Constants.OKHTTP.newCall(getPix("?key=30243195-5ec147e6ba62a277ce17ce78b&q=doctors&image_type=photo"))
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("onFailure: ", e.message.toString())
                        Snackbar.make(binding.root, "Server Error!", Snackbar.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                        val hits = json.getJSONArray("hits")
                        val gson = Gson().fromJson<List<Hits>>(
                            hits.toString(),
                            object : TypeToken<List<Hits>>() {}.type
                        )
                        requireActivity().runOnUiThread {
                            binding.vpDoctors.adapter = DoctorsAdapter(gson)
                            binding.vpDoctors.orientation = ViewPager2.ORIENTATION_HORIZONTAL
                        }
                        requireActivity().runOnUiThread {
                            binding.rvMedicines.adapter = DoctorsAdapter(gson)
                            binding.rvMedicines.layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                        }

                    }
                })
    }

    private fun obtainHistory() {
        val sharedPreferences =
            requireContext().getSharedPreferences(Constants.USER, Context.MODE_PRIVATE)
        Constants.OKHTTP.newCall(
            get(
                "symptoms_history?user_id=${
                    sharedPreferences.getString(
                        "id",
                        ""
                    )
                }"
            )
        ).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("onFailure: ", e.message.toString())
                    Snackbar.make(binding.root, "Server Error!", Snackbar.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call, response: Response) {
                    val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                    val data = json.getJSONArray("data")
                    val list = Gson().fromJson<List<DataHistory>>(
                        data.toString(),
                        object : TypeToken<List<DataHistory>>() {}.type
                    )
                    requireActivity().runOnUiThread {
                        binding.vpHistory.adapter = HistoryAdapter(list)
                        binding.vpHistory.orientation = ViewPager2.ORIENTATION_VERTICAL
                    }
                }
            })
    }

    private fun obtainCases() {
        Constants.OKHTTP.newCall(get("cases")).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
                Snackbar.make(binding.root, "Server Error!", Snackbar.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val num = (0..20).random()
                requireActivity().runOnUiThread {
                    if (num > 0) {
                        binding.txtNumCases.text = "$num Cases"
                    } else {
                        binding.txtNumCases.text = "No cases"
                        binding.llBgCases.backgroundTintList =
                            getColorStateList(requireContext(), R.color.light_dark_blue_200)
                    }
                }
            }
        })
    }

    private fun setDate() {
        binding.txtActualDate.text = SimpleDateFormat("MMM dd, yyyy").format(Date())
        Constants.actualDate = binding.txtActualDate.text.toString()
    }

}