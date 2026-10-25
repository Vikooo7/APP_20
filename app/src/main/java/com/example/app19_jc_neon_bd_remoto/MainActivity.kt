package com.example.app19_jc_neon_bd_remoto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.app19_jc_neon_bd_remoto.ui.theme.App19_jc_neon_bd_remotoTheme
import com.example.app19_jc_neon_bd_remoto.view.PantallaPersona

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App19_jc_neon_bd_remotoTheme {
                PantallaPersona()
            }
        }
    }
}
