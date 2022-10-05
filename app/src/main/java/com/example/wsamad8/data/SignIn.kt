package com.example.wsamad8.data

data class SignIn(
    val data: Data,
    val success: Boolean,
)

data class Data(
    val id: String,
    val login: String,
    val name: String,
    val token: String
)