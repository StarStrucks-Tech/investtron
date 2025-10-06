package com.example.krishna_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.krishna_project.navigation.AppNavigation
import com.example.krishna_project.session.SessionManager
import com.example.krishna_project.ui.theme.KrishnaprojectTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KrishnaprojectTheme {
                val startDestination = if (sessionManager.getUserId() != null) "dashboard" else "login"
                AppNavigation(startDestination = startDestination)
            }
        }
    }
}
