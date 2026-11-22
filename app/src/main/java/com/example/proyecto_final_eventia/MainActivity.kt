package com.example.proyecto_final_eventia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import com.example.proyecto_final_eventia.data.local.datastore.SessionDataStore
import com.example.proyecto_final_eventia.navigation.EventiaNavHost
import com.example.proyecto_final_eventia.ui.theme.Proyecto_Final_EventiaTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionDataStore: SessionDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Proyecto_Final_EventiaTheme {
                val token by sessionDataStore.token.collectAsState(initial = null)
                EventiaNavHost(token = token)
            }
        }
    }
}
