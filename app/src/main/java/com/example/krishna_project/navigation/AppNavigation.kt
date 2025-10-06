package com.example.krishna_project.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.krishna_project.ui.screens.DashboardScreen
import com.example.krishna_project.ui.screens.LoginScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(onLoginSuccess = { isNewUser ->
                scope.launch {
                    snackbarHostState.showSnackbar(
                        if (isNewUser) "Welcome to InvestoTon!" else "Welcome back!"
                    )
                }
                navController.navigate("dashboard") { 
                    popUpTo("login") { inclusive = true } 
                }
            })
        }
        composable("dashboard") {
            DashboardScreen(onLogout = {
                navController.navigate("login") { 
                    popUpTo("dashboard") { inclusive = true } 
                }
            })
        }
    }
}
