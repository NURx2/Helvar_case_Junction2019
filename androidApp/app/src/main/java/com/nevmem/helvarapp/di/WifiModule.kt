package com.nevmem.helvarapp.di

import android.content.Context
import com.nevmem.helvar.network.WifiAdapter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class WifiModule(private val ctx: Context) {
    @Provides
    @Singleton
    fun providesWifiAdapter() = WifiAdapter(ctx)
}