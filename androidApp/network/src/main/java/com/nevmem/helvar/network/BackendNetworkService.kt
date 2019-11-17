package com.nevmem.helvar.network

import android.os.Handler
import android.os.Looper
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class BackendNetworkService {
    companion object {
        const val baseUrl = "http://ab060bc6.ngrok.io"
    }

    var client = OkHttpClient.Builder()

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        client.addInterceptor(loggingInterceptor)
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val backendService = retrofit.create(BackendService::class.java)

    fun move(from: String, to: String) {
        val call = backendService.move(from.toLowerCase(), to.toLowerCase())
        call.enqueue(object: Callback<ControlSequenceResult> {
            override fun onFailure(call: Call<ControlSequenceResult>, t: Throwable) {
                move(from, to)
            }

            override fun onResponse(
                call: Call<ControlSequenceResult>,
                response: Response<ControlSequenceResult>) {
            }
        })
    }

    fun remoteControl(roomId: String, level: Int, temperature: Int) {
        val call = backendService.remoteControl(roomId, level.toString(), temperature.toString())
        call.enqueue(object: Callback<ControlSequenceResult> {
            override fun onFailure(call: Call<ControlSequenceResult>, t: Throwable) {
                Log.d("debug", "Error: " + t.message.toString())
                remoteControl(roomId, level, temperature)
            }

            override fun onResponse(
                call: Call<ControlSequenceResult>,
                response: Response<ControlSequenceResult>) {
                Log.d("debug", "code: " + response.code().toString())
            }
        })
    }

    fun remoteSmart() {
        val call = backendService.remoteSmart()
        call.enqueue(object: Callback<ControlSequenceResult> {
            override fun onFailure(call: Call<ControlSequenceResult>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<ControlSequenceResult>,
                response: Response<ControlSequenceResult>) {
            }
        })
    }

    fun switchOnEntrance() {
        val call = backendService.switchOnEntrance()
        call.enqueue(object: Callback<ControlSequenceResult> {
            override fun onFailure(call: Call<ControlSequenceResult>, t: Throwable) {
                Log.d("debug", "${t.message.toString()}")
                Handler(Looper.getMainLooper()).postDelayed({ switchOnEntrance() }, 100)
            }

            override fun onResponse(
                call: Call<ControlSequenceResult>,
                response: Response<ControlSequenceResult>) {
                Log.d("debug", "${response.code()}")
            }
        })
    }
}
