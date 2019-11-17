package com.nevmem.helvarapp.di

import com.nevmem.helvarapp.PswdActivity
import com.nevmem.helvarapp.StartActivity
import com.nevmem.helvarapp.activities.AddRoomActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    WifiModule::class,
    NetworkModule::class,
    DatabaseModule::class,
    BackgroundModule::class])
interface AppComponent {
    fun inject(pswdActivity: PswdActivity)
    fun inject(addRoomActivity: AddRoomActivity)
    fun inject(startActivity: StartActivity)
}