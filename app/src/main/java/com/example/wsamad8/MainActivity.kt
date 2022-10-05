package com.example.wsamad8

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wsamad8.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        animations()
    }

    private fun animations() {
        binding.logo.animate().translationY(-3000f).alpha(0f).setDuration(0).withEndAction {
            binding.logoName.animate().translationY(-100f).alpha(0f).setDuration(0).withEndAction {
                binding.logo.animate().translationY(0f).alpha(1f).setDuration(400).withEndAction {
                    binding.logoName.animate().translationY(0f).alpha(1f).setDuration(400).withEndAction {
                        binding.logoName.animate().alpha(1f).setDuration(500).withEndAction {
                            val i = Intent(this@MainActivity,LoginActivity::class.java)
                            startActivity(i)
                            finish()
                        }
                    }
                }
            }
        }
    }

}