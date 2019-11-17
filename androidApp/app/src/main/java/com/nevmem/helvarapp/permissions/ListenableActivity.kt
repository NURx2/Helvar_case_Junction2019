package com.nevmem.helvarapp.permissions

import androidx.appcompat.app.AppCompatActivity

abstract class ListenableActivity : AppCompatActivity() {
    interface ActivityRequestCodeListener {
        fun onResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    }

    private val listeners = HashMap<Int, ActivityRequestCodeListener>()
    fun setPermissionListener(requestCode: Int, listener: ActivityRequestCodeListener) {
        listeners[requestCode] = listener
    }
    fun removePermissionListener(requestCode: Int) {
        listeners.remove(requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        listeners[requestCode]?.onResult(requestCode, permissions, grantResults)
    }
}