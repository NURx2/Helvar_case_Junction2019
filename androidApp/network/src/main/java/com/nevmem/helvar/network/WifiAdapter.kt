package com.nevmem.helvar.network

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class WifiAdapter(ctx: Context) {
    companion object {
        const val wifiUpdatingDelay = 500L
    }

    private val wifiManager = ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val scanner = WifiScanner(wifiManager, ctx)

    interface WifiAdapterListener {
        fun wifiInfoChanged() {}
        fun connectedToHome() {}
    }

    fun wifiInfo(): WifiState = currentState

    private var prevSSID: String = ""

    private val listeners = ArrayList<WifiAdapterListener>()
    fun subscribe(listener: WifiAdapterListener) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: WifiAdapterListener) {
        listeners.remove(listener)
    }

    data class WifiState(val BSSID: String, val ssid: String) {
        companion object {
            val noWifiValue = WifiState("none", "none")
        }
    }

    private var currentState = WifiState("none", "none")

    init {
        val interval = Observable.interval(wifiUpdatingDelay, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())

        val processer = {
            val state = if (wifiManager.connectionInfo.bssid != null) {
                WifiState(
                    wifiManager.connectionInfo.bssid,
                    wifiManager.connectionInfo.ssid
                )
            } else {
                WifiState.noWifiValue
            }
            proceedChange(state)
        }

        val subsciption = interval.subscribe { processer() }
        processer()
    }

    private fun proceedChange(newState: WifiState) {
        currentState = newState
        Log.d("debug", "${currentState.ssid.toLowerCase().trim()} ${prevSSID.toLowerCase().trim()}")
        if (currentState.ssid.toLowerCase().trim() == "\"junction\"" && prevSSID.toLowerCase().trim() != "\"junction\"") {
            listeners.forEach {
                it.connectedToHome()
            }
        }
        prevSSID = currentState.ssid
        listeners.forEach {
            it.wifiInfoChanged()
        }
    }
}