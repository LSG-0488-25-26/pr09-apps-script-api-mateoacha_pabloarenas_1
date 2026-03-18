package com.example.pr09_app.data.repository

import com.example.pr09_app.data.network.ApiService
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class DatasetRepository(private val api: ApiService) {
    suspend fun fetchRows(endpoint: String): Result<List<Map<String, String>>> = runCatching {
        val json = api.get(
            mapOf(
                "endpoint" to endpoint
            )
        )
        parseRows(json)
    }

    private fun parseRows(json: JsonElement): List<Map<String, String>> {
        // Soporta respuestas típicas:
        // 1) Array directo: [ {..}, {..} ]
        // 2) Wrapper: { ok: true, data: [ {..} ] } o { data: [...] }
        val data = when {
            json.isJsonArray -> json.asJsonArray
            json.isJsonObject && json.asJsonObject.has("data") -> json.asJsonObject.getAsJsonArray("data")
            else -> null
        } ?: return emptyList()

        return data.mapNotNull { elem ->
            if (!elem.isJsonObject) return@mapNotNull null
            val obj: JsonObject = elem.asJsonObject
            obj.entrySet().associate { (k, v) ->
                k to (if (v.isJsonNull) "" else v.asString)
            }
        }
    }
}

