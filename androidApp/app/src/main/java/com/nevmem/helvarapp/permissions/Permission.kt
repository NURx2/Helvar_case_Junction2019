package com.nevmem.helvarapp.permissions

import android.Manifest


enum class Permission(val permission: String) {
    FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
    COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),
    INTERNET(Manifest.permission.INTERNET),
    CHANGE_WIFI_STATE(Manifest.permission.CHANGE_WIFI_STATE),
}