package com.example.pr09_app.data.network

import com.google.gson.JsonElement
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiService {
    /**
     * Base URL debería ser algo como:
     * - https://script.google.com/macros/s/<SCRIPT_ID>/
     * y aquí usamos "exec" como path.
     *
     * Si vuestra BASE_URL ya termina en /exec, cambiad @GET("exec") por @GET("")
     */
    @GET("exec")
    suspend fun get(@QueryMap params: Map<String, String>): JsonElement
}

