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
        val response = api.insertWorldCup(body)
        if (!response.isSuccessful) {
            throw RuntimeException("POST no fue exitoso. HTTP ${response.code()}")
        }
        Unit
    }
}

