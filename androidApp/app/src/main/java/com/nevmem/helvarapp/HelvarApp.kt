package com.nevmem.helvarapp

import android.app.Application
import com.nevmem.helvarapp.di.DaggerAppComponent
import com.nevmem.helvarapp.di.DatabaseModule
import com.nevmem.helvarapp.di.WifiModule

class HelvarApp : Application() {
    val appComponent = DaggerAppComponent.builder()
        .wifiModule(WifiModule(this))
        .databaseModule(DatabaseModule(this))
        .build()
}