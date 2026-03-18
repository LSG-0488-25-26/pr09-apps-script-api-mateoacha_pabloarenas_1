package com.example.pr09_app.data.repository

import com.example.pr09_app.BuildConfig
import com.example.pr09_app.data.model.WorldCup
import com.example.pr09_app.data.network.WorldCupApiService

class DatasetRepository(private val api: WorldCupApiService) {
    suspend fun fetchWorldCups(action: String): Result<List<WorldCup>> = runCatching {
        api.getAllData(
            key = BuildConfig.API_KEY,
            action = action,
        )
    }

    suspend fun insertWorldCup(body: Map<String, Any>): Result<Unit> = runCatching {
        // doPost(e) valida `postData.key` (no `e.parameter.key`), así que lo metemos en el body.
        val payload = body.toMutableMap().apply { put("key", BuildConfig.API_KEY) }

        val response = api.insertWorldCup(payload)
        if (!response.isSuccessful) {
            throw RuntimeException("POST no fue exitoso. HTTP ${response.code()}")
        }
        Unit
    }
}

