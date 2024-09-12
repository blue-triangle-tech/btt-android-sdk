package com.bluetriangle.android.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bluetriangle.analytics.okhttp.bttTrack
import com.bluetriangle.android.demo.databinding.ActivityNetworkPocBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.time.Duration
import java.util.concurrent.TimeUnit

class NetworkPocActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNetworkPocBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val okHttpClient = OkHttpClient.Builder()
            .bttTrack()
            .callTimeout(10, TimeUnit.SECONDS)
            .build()
        binding.pocHandler = NetworkPocHandler(lifecycleScope, okHttpClient)

        binding.postAPI.setOnClickListener {
            val body: RequestBody = FormBody.Builder().add("test", "value").build()
            val postRequest: Request =
                Request.Builder().url("https://httpbin.org/post").method("POST", body).build()

            okHttpClient.makeRequest(postRequest)
        }
        binding.api404.setOnClickListener {
            val failureRequest = Request.Builder()
                .url("https://httpbin.org/invalidendpoint")
                .build()
            okHttpClient.makeRequest(failureRequest)
        }
        binding.getAPI.setOnClickListener {
            val getRequest: Request = Request.Builder().url("https://www.httpbin.org/json").build()
            okHttpClient.makeRequest(getRequest)
        }

    }


}

private fun OkHttpClient.makeRequest(successRequest: Request) {
    newCall(successRequest)
        .enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d("OkHttpEventListener", "Failure: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("OkHttpEventListener", "Response: ${response.code}, ${response.body?.bytes()?.decodeToString()}")
            }
        })
}
