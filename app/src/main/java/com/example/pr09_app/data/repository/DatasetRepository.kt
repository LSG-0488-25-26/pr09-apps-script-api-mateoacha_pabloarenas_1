package com.example.pr09_app.data.repository

import com.example.pr09_app.BuildConfig
import com.example.pr09_app.data.model.WorldCup
import com.example.pr09_app.data.network.WorldCupApiService

/**
 * Repository que encapsula la lógica de acceso a datos
 * Actúa como intermediaria entre la UI (ViewModel) y la red (Retrofit)
 */
class DatasetRepository(private val api: WorldCupApiService) {
    /**
     * Obtiene lista de mundiales
     * @param filterCountry País opcional para filtrar
     * @return Result<List<WorldCup>>
     */
    suspend fun fetchWorldCups(filterCountry: String? = null): Result<List<WorldCup>> = runCatching {
        val response = api.getWorldCups(
            apiKey = BuildConfig.API_KEY,
            type = "worldcups",
            country = filterCountry
        )
        
        if (response.status == "ok" && response.data != null) {
            response.data
        } else {
            throw Exception(response.error ?: "Error desconocido al obtener datos")
        }
    }

    /**
     * Inserta un nuevo registro de mundial
     * @param body Map con campos: year, country, winner, runnerup, third, fourth, goals, qualified, matches, attendance
     * @return Result<Unit>
     */
    suspend fun insertWorldCup(body: Map<String, Any>): Result<Unit> = runCatching {
        val payload = body.toMutableMap().apply { 
            put("apiKey", BuildConfig.API_KEY) 
        }

        val response = api.insertWorldCup(payload)
        if (response.status == "ok") {
            Unit
        } else {
            throw Exception(response.error ?: "Error al insertar registro")
        }
    }
}

