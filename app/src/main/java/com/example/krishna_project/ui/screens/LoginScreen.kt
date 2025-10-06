package com.example.krishna_project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.krishna_project.ui.viewmodel.LoginViewModel
import com.example.krishna_project.ui.viewmodel.LoginState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: (isNewUser: Boolean) -> Unit, viewModel: LoginViewModel = hiltViewModel()) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isFounder by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    val loginState by viewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.userExists(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("Enter your ID") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter your password") },
            visualTransformation = PasswordVisualTransformation()
        )

        if (loginState is LoginState.UserNotFound) {
            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = if (isFounder) "Founder" else "Investor",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Role") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("Founder") }, onClick = { 
                        isFounder = true
                        expanded = false
                    })
                    DropdownMenuItem(text = { Text("Investor") }, onClick = { 
                        isFounder = false
                        expanded = false
                    })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { 
            val role = if (isFounder) "Founder" else "Investor"
            viewModel.onLogin(userId, password, if (loginState is LoginState.UserNotFound) role else null)
        }) {
            Text(if (loginState is LoginState.UserNotFound) "Register" else "Login")
        }

        LaunchedEffect(loginState) {
            when (loginState) {
                is LoginState.Success -> onLoginSuccess((loginState as LoginState.Success).isNewUser)
                is LoginState.Error -> {
                    snackbarHostState.showSnackbar(
                        message = "Login failed, please try again.",
                        duration = SnackbarDuration.Short
                    )
                }
                else -> {}
            }
        }
    }
    SnackbarHost(snackbarHostState, modifier = Modifier.fillMaxWidth().wrapContentHeight(Alignment.Bottom))
}
