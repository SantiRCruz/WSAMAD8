package com.example.wsamad8

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.util.PatternsCompat
import androidx.core.widget.addTextChangedListener
import com.example.wsamad8.core.Constants
import com.example.wsamad8.core.networkInfo
import com.example.wsamad8.data.SignIn
import com.example.wsamad8.data.post
import com.example.wsamad8.data.signIn
import com.example.wsamad8.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        clicks()
        writing()
    }

    private fun writing() {
        emailWriting()
    }

    private fun emailWriting() {
        val regex = Pattern.compile("^([a-zA-Z@.]){1,20}")
        binding.edtEmail.addTextChangedListener {
            if (it.toString().isNullOrEmpty()){
                binding.edtEmail.error = "This field can't be empty"
            }else if(!regex.matcher(it!!).matches()){
                binding.edtEmail.error = "Can't have more than 10 letters"
            }else{
                binding.edtEmail.error = null
            }
        }
    }

    private fun clicks() {
        binding.btnSignIn.setOnClickListener { validate() }
    }

    private fun validate() {
        val results = arrayOf(validateEmail(),validatePassword())
        if (false in results) return

        if (!networkInfo(applicationContext)){
            Snackbar.make(binding.root,"You don't have connection",Snackbar.LENGTH_SHORT).show()
            return
        }

        visibilityProgress(true)
        sendSignIn()
    }

    private fun visibilityProgress(b: Boolean) {
        if (b){
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSignIn.visibility = View.GONE
        }else{
            binding.progressBar.visibility = View.GONE
            binding.btnSignIn.visibility = View.VISIBLE
        }
    }

    private fun sendSignIn() {
        Constants.OKHTTP.newCall(post("signin", signIn(binding.edtEmail.text.toString(),binding.edtPassword.text.toString()))).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("onFailure: ", e.message.toString())
                    Snackbar.make(binding.root,"Server Error!",Snackbar.LENGTH_SHORT).show()

                }

                override fun onResponse(call: Call, response: Response) {
                    val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                    val data = Gson().fromJson(json.toString(),SignIn::class.java)
                    if (data.success ){
                        val sharedPreferences = getSharedPreferences(Constants.USER,Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()){
                            putString("id",data.data.id)
                            putString("login",data.data.login)
                            putString("name",data.data.name)
                            putString("token",data.data.token)
                            apply()
                        }
                        val i = Intent(this@LoginActivity,HomeActivity::class.java)
                        startActivity(i)
                    }else{
                        Snackbar.make(binding.root,"Wrong Credentials!",Snackbar.LENGTH_SHORT).show()
                    }
                    runOnUiThread {
                        visibilityProgress(false)
                    }
                }
            })
    }

    private fun validatePassword(): Boolean {
        return if (binding.edtEmail.text.toString().isNullOrEmpty()){
            Snackbar.make(binding.root,"Any field can't be empty",Snackbar.LENGTH_SHORT).show()
            false
        }else{
            true
        }
    }

    private fun validateEmail(): Boolean {
        return if (binding.edtEmail.text.toString().isNullOrEmpty()){
            Snackbar.make(binding.root,"Any field can't be empty",Snackbar.LENGTH_SHORT).show()
            false
        }else if(!PatternsCompat.EMAIL_ADDRESS.matcher(binding.edtEmail.text.toString()).matches()){
            Snackbar.make(binding.root,"The email Field must have an email format",Snackbar.LENGTH_SHORT).show()
            false
        }else{
            true
        }
    }
}