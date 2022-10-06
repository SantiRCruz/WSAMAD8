package com.example.wsamad8.data

import android.hardware.usb.UsbEndpoint
import android.net.Uri
import com.example.wsamad8.core.Constants
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.util.*

fun post(endpoint: String,body: RequestBody):Request{
    return Request.Builder().url("${Constants.URL}/$endpoint").post(body).build()
}
fun postSendPhoto(endpoint: String,body: RequestBody):Request{
    return Request.Builder().url(endpoint).post(body).build()
}
fun photo(uri:Uri):RequestBody{
    return MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file","${UUID.randomUUID()}.jpg",File(uri.path!!).asRequestBody()).build()
}

fun signIn(email:String,password:String):RequestBody{
    val json = JSONObject().apply {
        put("login",email)
        put("password",password)
    }
    return json.toString().toRequestBody("application/json".toMediaType())
}

fun get(endpoint: String):Request{
    return Request.Builder().url("${Constants.URL}/$endpoint").get().build()
}
fun getPix(endpoint: String):Request{
    return Request.Builder().url("https://pixabay.com/api/$endpoint").get().build()
}