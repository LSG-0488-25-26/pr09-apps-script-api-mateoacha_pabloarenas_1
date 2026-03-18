package com.example.pr09_app.data.repository

import com.example.pr09_app.BuildConfig
import com.example.pr09_app.data.network.ApiService
import com.google.gson.JsonElement

class DatasetRepository(private val api: ApiService) {
    suspend fun fetchRows(type: String): Result<List<Map<String, String>>> = runCatching {
        val response = api.getData(
            apiKey = BuildConfig.API_KEY,
            type = type
        )

        if (response.status != "ok") {
            throw RuntimeException(response.error ?: "Error desconocido")
        }

        val data = response.data.orEmpty()
        data.map { row ->
            row.mapValues { (_, value) -> jsonToString(value) }
        }
    }

    private fun jsonToString(element: JsonElement): String {
        return when {
            element.isJsonNull -> ""
            element.isJsonPrimitive -> element.asString
            else -> element.toString()
        }
    }
}

