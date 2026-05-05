package com.example.ugawaka

import android.app.Application

class UgaWakaApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: IProviderRepository by lazy { ProviderRepository(database) }
}
