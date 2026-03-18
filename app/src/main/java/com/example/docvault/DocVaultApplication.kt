package com.example.docvault

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DocVaultApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}