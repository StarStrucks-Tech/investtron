package com.example.krishna_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishna_project.data.local.User
import com.example.krishna_project.data.repository.PostRepository
import com.example.krishna_project.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun onLogin(userId: String, password: String, role: String? = null) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val user = postRepository.getUserById(userId)
            if (user == null) {
                if (role != null) {
                    val passwordHash = postRepository.getPasswordHash(password)
                    val newUser = User(userId = userId, passwordHash = passwordHash, role = role)
                    postRepository.insertUser(newUser)
                    sessionManager.saveSession(userId, role)
                    _loginState.value = LoginState.Success(isNewUser = true)
                } else {
                    _loginState.value = LoginState.UserNotFound
                }
            } else {
                val passwordHash = postRepository.getPasswordHash(password)
                if (user.passwordHash == passwordHash) {
                    sessionManager.saveSession(userId, user.role)
                    _loginState.value = LoginState.Success(isNewUser = false)
                } else {
                    _loginState.value = LoginState.Error("Invalid password")
                }
            }
        }
    }

    fun userExists(userId: String) {
        viewModelScope.launch {
            val user = postRepository.getUserById(userId)
            _loginState.value = if (user != null) LoginState.UserFound else LoginState.UserNotFound
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val isNewUser: Boolean) : LoginState()
    data class Error(val message: String) : LoginState()
    object UserNotFound : LoginState()
    object UserFound : LoginState()
}
