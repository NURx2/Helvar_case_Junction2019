package com.nevmem.helvarapp.permissions

import android.content.pm.PackageManager
import android.util.Log

class PermissionManager(private val activity: ListenableActivity) : ListenableActivity.ActivityRequestCodeListener {
    companion object {
        const val defaultRequestCode = 123
    }

    init {
        activity.setPermissionListener(defaultRequestCode, this)
    }

    private var currentCallback: ((allGranted: Boolean) -> Unit)? = null
    private var currentAsking: List<Permission>? = null

    fun requestPermissions(permissions: List<Permission>, cb: (allGranted: Boolean) -> Unit) {
        val canAsk = permissions.filter{ !hasPermission(it) }

        currentCallback = cb
        currentAsking = permissions

        if (canAsk.isNotEmpty()) {
            activity.requestPermissions(
                canAsk.map { it.permission }.toTypedArray(),
                defaultRequestCode
            )
        } else {
            proceedResult()
        }
    }

    override fun onResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        proceedResult()
    }

    private fun proceedResult() {
        currentAsking?.let { permissions->
            currentCallback?.invoke(permissions.all { hasPermission(it) })
            return
        }
        currentCallback?.invoke(false)
    }

    fun hasPermission(permission: Permission): Boolean {
        return activity.checkSelfPermission(permission.permission) == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermissionRatioanale(permission: Permission): Boolean {
        return activity.shouldShowRequestPermissionRationale(permission.permission)
    }
}