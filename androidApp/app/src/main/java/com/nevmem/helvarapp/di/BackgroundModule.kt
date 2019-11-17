package com.nevmem.helvarapp.di

import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Named
import javax.inject.Singleton

@Module
class BackgroundModule {
    companion object {
        const val backgroundThreadPoolSize = 4
    }

    @Provides
    @Singleton
    @Named("background")
    fun provideExecutor(): Executor
        = Executors.newFixedThreadPool(backgroundThreadPoolSize)
}