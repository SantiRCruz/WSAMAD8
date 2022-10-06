package com.example.wsamad8.core

import androidx.core.util.PatternsCompat
import com.google.android.material.snackbar.Snackbar

object Validations {
    private fun validateEmail(s:String?): Boolean {
        return if (s.isNullOrEmpty()){
            false
        }else if(!PatternsCompat.EMAIL_ADDRESS.matcher(s).matches()){
            false
        }else{
            true
        }
    }
}