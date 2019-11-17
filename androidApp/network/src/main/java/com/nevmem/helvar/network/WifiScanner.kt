package com.nevmem.helvar.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class WifiScanner(private val wifiManager: WifiManager, ctx: Context) {
    companion object {
        const val maxValue = 1000000
        const val intervalDelay = 500L
        val mostValuableCount = 20
    }

    private lateinit var scanningDisposable: Disposable

    val scanResults = BehaviorSubject.create<List<Pair<Int, String>>>()

    init {
        val scannerReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent == null) {
                    return
                }
                if (intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) {
                    proceedScanResults(wifiManager.scanResults, true)
                } else {
                    proceedScanResults(wifiManager.scanResults, false)
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        ctx.registerReceiver(scannerReceiver, intentFilter)
    }

    fun startScanning() {
        scanningDisposable = Observable.interval(intervalDelay, TimeUnit.MILLISECONDS)
            .subscribe {
                val success = wifiManager.startScan()
                Log.d("debug", "$success")
            }
    }

    fun stopScanning() {
        scanningDisposable.dispose()
    }

    private fun proceedScanResults(list: List<ScanResult>, realTime: Boolean) {
        Log.d("debug", "processing scan results $realTime")
        if (!realTime) {
            return
        }
        val map = HashMap<String, Int>()
        list.forEach {
            map[it.SSID] = WifiManager.calculateSignalLevel(it.level, maxValue)
        }
        val mostValuable = map
            .map { Pair(it.value, it.key) }
            .sortedBy { it.first }
            .reversed()
            .slice(0 .. kotlin.math.min(mostValuableCount, map.size - 1))
        scanResults.onNext(mostValuable)
    }
}