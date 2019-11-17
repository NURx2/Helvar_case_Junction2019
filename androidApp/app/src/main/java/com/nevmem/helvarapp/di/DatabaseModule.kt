package com.nevmem.helvarapp.di

import android.content.Context
import androidx.room.Room
import com.nevmem.helvarapp.data.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(private val ctx: Context) {
    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase
        = Room.databaseBuilder(ctx, AppDatabase::class.java, "rooms_db").build()
}