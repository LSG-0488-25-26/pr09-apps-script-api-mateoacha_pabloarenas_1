package com.example.pr09_app.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.JsonElement

/**
 * Modelo genérico para respuestas del Apps Script (doGet/doPost).
 * Se alinea con la estructura típica:
 * { status: "ok", type: "...", data: ..., error: null }
 */
data class GetResponse<T>(
    val status: String,
    val type: String? = null,
    val data: T? = null,
    val error: String? = null,
)

interface ApiService {
    // GET de datos: el parámetro `type` permite seleccionar endpoint "lógico" dentro de doGet(e).
    @GET("exec")
    suspend fun getData(
        @Query("apiKey") apiKey: String,
        @Query("type") type: String,
    ): GetResponse<List<Map<String, JsonElement>>>
}

