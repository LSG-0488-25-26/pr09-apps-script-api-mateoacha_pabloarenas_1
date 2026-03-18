package com.example.pr09_app.data.repository

import com.example.pr09_app.BuildConfig
import com.example.pr09_app.data.model.InsertWorldCupRequest
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
        // Convertimos el Map de la UI a un request tipado para evitar problemas de Retrofit
        // con `Map<String, Any>` (wildcards / type variables).
        val request = InsertWorldCupRequest(
            apiKey = BuildConfig.API_KEY,
            year = (body["year"] as? Int) ?: (body["year"] as? Number)?.toInt() ?: 0,
            country = (body["country"] as? String) ?: "",
            winner = (body["winner"] as? String) ?: "",
            runnerup = (body["runnerup"] as? String) ?: "",
            third = (body["third"] as? String) ?: "",
            fourth = (body["fourth"] as? String) ?: "",
            goals = (body["goals"] as? Int) ?: (body["goals"] as? Number)?.toInt() ?: 0,
            qualified = (body["qualified"] as? Int) ?: (body["qualified"] as? Number)?.toInt() ?: 0,
            matches = (body["matches"] as? Int) ?: (body["matches"] as? Number)?.toInt() ?: 0,
            attendance = (body["attendance"] as? String) ?: ""
        )

        val response = api.insertWorldCup(request)
        if (response.status == "ok") {
            Unit
        } else {
            throw Exception(response.error ?: "Error al insertar registro")
        }
    }
}

