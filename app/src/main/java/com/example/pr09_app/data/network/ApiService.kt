package com.example.pr09_app.data.network

import com.example.pr09_app.data.model.GetResponse
import com.example.pr09_app.data.model.InsertWorldCupData
import com.example.pr09_app.data.model.InsertWorldCupRequest
import com.example.pr09_app.data.model.WorldCup
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Interface Retrofit para consumir la API de Apps Script
 * 
 * Define los endpoints GET y POST que conectan con Google Apps Script
 * 
 * Endpoints:
 * - GET exec?apiKey=...&type=worldcups → Lista todos mundiales
 * - GET exec?apiKey=...&type=worldcups&country=PAIS → Filtra por país
 * - POST exec → Inserta nuevo registro
 */
interface WorldCupApiService {
    /**
     * GET - Obtiene lista de mundiales
     * 
     * @param apiKey Clave de autenticación (variables de entorno desde secrets.properties)
     * @param type Tipo de consulta: "worldcups"
     * @param country (Opcional) Filtro por país anfitrión
     * @return GetResponse<List<WorldCup>> - Respuesta estándar con datos
     */
    @GET("exec")
    suspend fun getWorldCups(
        @Query("apiKey") apiKey: String,
        @Query("type") type: String = "worldcups",
        @Query("country") country: String? = null
    ): GetResponse<List<WorldCup>>

    /**
     * POST - Inserta nuevo registro de mundial
     * 
     * @param body Request tipado con los campos: year, country, winner, runnerup, third, fourth, goals, qualified, matches, attendance, apiKey
     * @return GetResponse con confirmación
     */
    @POST("exec")
    suspend fun insertWorldCup(
        @Body body: InsertWorldCupRequest
    ): GetResponse<InsertWorldCupData>
}

