package com.nevmem.helvarapp.di

import com.nevmem.helvar.network.BackendNetworkService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkBackendService() = BackendNetworkService()
}