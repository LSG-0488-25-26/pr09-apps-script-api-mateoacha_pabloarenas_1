package com.example.pr09_app.data.auth

import android.content.Context
import android.content.SharedPreferences

class AuthStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = !currentUser().isNullOrBlank()

    fun currentUser(): String? = prefs.getString(KEY_CURRENT_USER, null)

    fun logout() {
        prefs.edit().remove(KEY_CURRENT_USER).apply()
    }

    fun register(username: String, password: String): RegisterResult {
        val u = username.trim()
        val p = password.trim()

        if (u.isBlank() || p.isBlank()) return RegisterResult.InvalidInput
        if (prefs.contains(userKey(u))) return RegisterResult.UserAlreadyExists

        prefs.edit()
            .putString(userKey(u), p)
            .apply()

        return RegisterResult.Success
    }

    fun login(username: String, password: String): LoginResult {
        val u = username.trim()
        val p = password.trim()

        if (u.isBlank() || p.isBlank()) return LoginResult.InvalidInput

        val saved = prefs.getString(userKey(u), null) ?: return LoginResult.BadCredentials
        if (saved != p) return LoginResult.BadCredentials

        prefs.edit().putString(KEY_CURRENT_USER, u).apply()
        return LoginResult.Success
    }

    private fun userKey(username: String) = "user:$username"

    companion object {
        private const val PREFS_NAME = "users"
        private const val KEY_CURRENT_USER = "currentUser"
    }
}

sealed interface LoginResult {
    data object Success : LoginResult
    data object InvalidInput : LoginResult
    data object BadCredentials : LoginResult
}

sealed interface RegisterResult {
    data object Success : RegisterResult
    data object InvalidInput : RegisterResult
    data object UserAlreadyExists : RegisterResult
}

