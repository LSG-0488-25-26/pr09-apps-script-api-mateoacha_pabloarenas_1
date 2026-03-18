package com.example.pr09_app.data.model

/**
 * Request tipado para el POST del Apps Script.
 *
 * Evita usar `Map<String, Any>` como body en Retrofit, ya que puede provocar errores
 * de tipos (wildcards / type variables) durante el análisis del método.
 */
data class InsertWorldCupRequest(
    val apiKey: String,
    val year: Int,
    val country: String,
    val winner: String,
    val runnerup: String = "",
    val third: String = "",
    val fourth: String = "",
    val goals: Int = 0,
    val qualified: Int = 0,
    val matches: Int = 0,
    val attendance: String = ""
)

/**
 * Solo necesitamos algunos campos del response del servidor.
 */
data class InsertWorldCupData(
    val inserted: Boolean? = null,
    val year: Int? = null,
    val country: String? = null
)

