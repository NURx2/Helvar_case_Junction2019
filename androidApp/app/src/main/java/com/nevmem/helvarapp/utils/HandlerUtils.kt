package com.nevmem.helvarapp.utils

import android.os.Handler
import android.os.Looper

fun delayedOnUI(delay: Long, cb: ()->Unit) {
    Handler(Looper.getMainLooper()).postDelayed(cb, delay)
}

fun runOnUi(cb: ()->Unit) {
    Handler(Looper.getMainLooper()).post(cb)
}