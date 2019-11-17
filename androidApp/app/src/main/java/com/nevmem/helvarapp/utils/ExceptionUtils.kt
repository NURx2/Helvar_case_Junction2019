package com.nevmem.helvarapp.utils

fun infiniteTry(cb: (onError: ()->Unit)->Unit) {
    cb { infiniteTry(cb) }
}