package com.nevmem.helvar.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


data class ControlSequenceResult(val result: String)

interface BackendService {
    @GET("/remote_control/{id}/{level}/{temperature}")
    fun remoteControl(
        @Path("id") id: String,
        @Path("level") level: String,
        @Path("temperature") temp: String): Call<ControlSequenceResult>

    @GET("/move/{from}/{to}")
    fun move(@Path("from") from: String, @Path("to") to: String): Call<ControlSequenceResult>

    @GET("r/emote_smart_demo")
    fun remoteSmart(): Call<ControlSequenceResult>

    @GET("/remote_enter")
    fun switchOnEntrance(): Call<ControlSequenceResult>
}