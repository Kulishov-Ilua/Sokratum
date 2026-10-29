package ru.kulishov.sokratum

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import ru.kulishov.sokratum.di.initKoin

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MyApplication)
            androidLogger()
        }
    }
}