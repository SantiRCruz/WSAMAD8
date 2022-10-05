package com.example.wsamad8

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wsamad8.databinding.ActivityHomeBinding
import com.example.wsamad8.databinding.FragmentHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

    }
}