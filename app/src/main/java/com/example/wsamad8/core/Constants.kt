package com.example.wsamad8.core

import okhttp3.OkHttpClient

object Constants {
    val OKHTTP = OkHttpClient.Builder().build()
    const val URL = "http://162.243.170.39/api"
    const val USER = "user"
    var actualDate :String ? = null
}