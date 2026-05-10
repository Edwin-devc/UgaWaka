package com.example.ugawaka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ugawaka.data.remote.RetrofitClient
import com.example.ugawaka.ui.theme.UgaWakaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            UgaWakaTheme {
                UgaWakaApp()
            }
        }
    }
}
