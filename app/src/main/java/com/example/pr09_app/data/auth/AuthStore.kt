package com.example.pr09_app.data.auth

class AuthStore(
    private val settingsRepository: SettingsRepository,
) {
    private val currentUserKey: String = KEY_CURRENT_USER

    fun isLoggedIn(): Boolean = currentUser().isNotBlank()

    fun currentUser(): String =
        settingsRepository.getSettingValue(currentUserKey, "")

    fun logout() {
        settingsRepository.removeSetting(KEY_CURRENT_USER)
    }

    fun register(username: String, password: String): RegisterResult {
        val u = username.trim()
        val p = password.trim()

        if (u.isBlank() || p.isBlank()) return RegisterResult.InvalidInput
        if (settingsRepository.contains(userKey(u))) return RegisterResult.UserAlreadyExists

        settingsRepository.saveSettingValue(userKey(u), p)

        return RegisterResult.Success
    }

    fun login(username: String, password: String): LoginResult {
        val u = username.trim()
        val p = password.trim()

        if (u.isBlank() || p.isBlank()) return LoginResult.InvalidInput

        val saved = settingsRepository.getSettingValue(userKey(u), "")
        if (saved.isBlank() || saved != p) return LoginResult.BadCredentials

        settingsRepository.saveSettingValue(KEY_CURRENT_USER, u)
        return LoginResult.Success
    }

    private fun userKey(username: String) = "user:$username"

    companion object {
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

