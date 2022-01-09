package com.example.magicpintest

import android.app.Application
import com.example.magicpintest.di.app_module
import com.example.magicpintest.di.single_module
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MagicNotesApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(app_module + single_module)
            androidContext(this@MagicNotesApp)
        }
    }
}