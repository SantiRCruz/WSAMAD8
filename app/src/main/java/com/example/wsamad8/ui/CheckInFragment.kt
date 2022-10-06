package com.example.wsamad8.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wsamad8.R
import com.example.wsamad8.core.Constants
import com.example.wsamad8.data.get
import com.example.wsamad8.data.models.Symptom
import com.example.wsamad8.data.photo
import com.example.wsamad8.data.post
import com.example.wsamad8.data.postSendPhoto
import com.example.wsamad8.databinding.FragmentCheckInBinding
import com.example.wsamad8.databinding.ItemBottomDialogBinding
import com.example.wsamad8.databinding.ItemHistoryBinding
import com.example.wsamad8.ui.adapters.SymptomAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class CheckInFragment : Fragment(R.layout.fragment_check_in) {
    private lateinit var binding: FragmentCheckInBinding
    private var uriResult: Uri? = null
    private val registerPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (false in it.values) {
                Snackbar.make(
                    binding.root,
                    "You must enable the permissions",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                permissions()
            }
        }
    private val registerCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data?.extras?.get("data") as Bitmap

            val wrapper = ContextWrapper(requireContext())
            var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
            file = File(file, "${UUID.randomUUID()}.jpg")
            val stream = FileOutputStream(file)
            data.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

            uriResult = Uri.parse(file.absolutePath)
            binding.imgAdd.setImageURI(uriResult)
        }
    private val registerGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data?.data
            data?.let { uri ->
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                val cursor =
                    requireContext().contentResolver.query(uri, projection, null, null, null)
                val column = cursor!!.getColumnIndex(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) uriResult = Uri.parse(cursor.getString(column))
                binding.imgAdd.setImageURI(uriResult)
            }
        }

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
                val gson = Gson().fromJson<List<Symptom>>(
                    list.toString(),
                    object : TypeToken<List<Symptom>>() {}.type
                )
                requireActivity().runOnUiThread {
                    binding.rvList.adapter = SymptomAdapter(gson)
                    binding.rvList.layoutManager = LinearLayoutManager(requireContext())
                }
            }
        })
    }

    private fun clicks() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.imgAdd.setOnClickListener { permissions() }
        binding.btnConfirm.setOnClickListener { validate() }

    }

    private fun validate() {
        if(uriResult == null){
            Snackbar.make(
                binding.root,
                "You must select a image",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }
        Log.e("validate: ", (binding.rvList.adapter as SymptomAdapter).checked().toString())
        sendPhoto()
        visibleProgress(true)
    }

    private fun visibleProgress(b:Boolean) {
        if (b){
            binding.progress.visibility = View.VISIBLE
            binding.btnConfirm.visibility = View.GONE
        }else{
            binding.progress.visibility = View.GONE
            binding.btnConfirm.visibility = View.VISIBLE
        }
    }

    private fun sendPhoto() {
        Constants.OKHTTP.newCall(postSendPhoto("https://cloudlabs-image-object-detection.p.rapidapi.com/objectDetection/byImageFile",
            photo(uriResult!!))).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
                Snackbar.make(binding.root, "Server Error!", Snackbar.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                requireActivity().runOnUiThread {
                    visibleProgress(false)
                }
                if (json.getString("status")== "success"){
                    Snackbar.make(binding.root,"You send the photo correctly",Snackbar.LENGTH_SHORT).show()
                }else{
                    requireActivity().runOnUiThread {
                        findNavController().popBackStack()
                    }
                }
            }
        })
    }

    private fun permissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                bottomDialog()
            }
            else -> {
                registerPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA
                    )
                )
            }
        }
    }

    private fun bottomDialog() {
        val dialog = ItemBottomDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val alertDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetTheme)
        with(alertDialog) {
            setContentView(dialog.root)
        }
        dialog.llCamera.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK)
            i.type = "image/*"
            registerGallery.launch(i)
            alertDialog.dismiss()
        }
        dialog.llGallery.setOnClickListener {
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            registerCamera.launch(i)
            alertDialog.dismiss()

        }
        alertDialog.show()

    }

    private fun setDate() {
        if (Constants.actualDate == null) {
            binding.txtActualDate.text = SimpleDateFormat("MMM dd, yyyy").format(Date())
        } else {
            binding.txtActualDate.text = Constants.actualDate
        }
    }

}