package com.example.pr09_app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pr09_app.data.auth.AuthStore
import com.example.pr09_app.data.auth.LoginResult
import com.example.pr09_app.data.auth.RegisterResult

class AuthViewModel(private val store: AuthStore) : ViewModel() {
    private val _uiState = MutableLiveData(AuthUiState())
    val uiState: LiveData<AuthUiState> = _uiState

    fun setUsername(value: String) {
        _uiState.value = _uiState.value?.copy(username = value, message = null)
    }

    fun setPassword(value: String) {
        _uiState.value = _uiState.value?.copy(password = value, message = null)
    }

    fun login() {
        val state = _uiState.value ?: return
        val result = store.login(state.username, state.password)
        _uiState.value = when (result) {
            LoginResult.Success -> state.copy(isLoggedIn = true, message = null)
            LoginResult.InvalidInput -> state.copy(message = "Usuario y contraseña obligatorios")
            LoginResult.BadCredentials -> state.copy(message = "Credenciales incorrectas")
        }
    }

    fun register() {
        val state = _uiState.value ?: return
        val result = store.register(state.username, state.password)
        _uiState.value = when (result) {
            RegisterResult.Success -> state.copy(message = "Usuario registrado. Ya puedes iniciar sesión.")
            RegisterResult.InvalidInput -> state.copy(message = "Usuario y contraseña obligatorios")
            RegisterResult.UserAlreadyExists -> state.copy(message = "Ese usuario ya existe")
        }
    }

    fun logout() {
        store.logout()
        _uiState.value = AuthUiState()
    }

    fun refreshSession() {
        _uiState.value = _uiState.value?.copy(isLoggedIn = store.isLoggedIn())
    }
}

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val isLoggedIn: Boolean = false,
    val message: String? = null,
)

