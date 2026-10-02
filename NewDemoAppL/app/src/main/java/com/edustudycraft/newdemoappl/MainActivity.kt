package com.edustudycraft.newdemoappl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.edustudycraft.newdemoappl.di.LearningViewModelFactory
import com.edustudycraft.newdemoappl.presentation.LocalViewModelFactory
import com.edustudycraft.newdemoappl.presentation.navigation.LearnNavHost
import com.edustudycraft.newdemoappl.ui.theme.NewDemoAppLTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val factory = LearningViewModelFactory((application as LearningApp).container)
        setContent {
            NewDemoAppLTheme {
                CompositionLocalProvider(LocalViewModelFactory provides factory) {
                    LearnNavHost()
                }
            }
        }
    }
}
