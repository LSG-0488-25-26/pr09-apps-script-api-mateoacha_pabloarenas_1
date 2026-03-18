package com.example.pr09_app.data.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * Repositorio para leer/escribir datos en SharedPreferences.
 * Evita que el ViewModel tenga acceso directo a Context.
 */
class SettingsRepository(
    nomFitxer: String,
    context: Context,
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(nomFitxer, Context.MODE_PRIVATE)

    fun <T> saveSettingValue(key: String, value: T) {
        with(sharedPreferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
            apply()
        }
    }

    fun <T> getSettingValue(key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(key, defaultValue) as T
            is Int -> sharedPreferences.getInt(key, defaultValue) as T
            is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
            is Float -> sharedPreferences.getFloat(key, defaultValue) as T
            is Long -> sharedPreferences.getLong(key, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    fun removeSetting(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    fun contains(key: String): Boolean = sharedPreferences.contains(key)

    fun guardarNom(valorNom: String) {
        saveSettingValue(KEY_NOM_USUARI, valorNom)
    }

    fun obtenerNom(): String = getSettingValue(KEY_NOM_USUARI, "")

    companion object {
        private const val KEY_NOM_USUARI = "nom_usuari"
    }
}

