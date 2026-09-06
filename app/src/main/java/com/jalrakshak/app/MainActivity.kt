package com.jalrakshak.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jalrakshak.app.core.design.AppShell
import com.jalrakshak.app.ui.theme.JalRakshakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JalRakshakTheme {
                AppShell()
            }
        }
    }
}
