package com.example.pr09_app.data.repository

import com.example.pr09_app.BuildConfig
import com.example.pr09_app.data.model.WorldCup
import com.example.pr09_app.data.network.WorldCupApiService
import okhttp3.ResponseBody
import retrofit2.Response

class DatasetRepository(private val api: WorldCupApiService) {
    suspend fun fetchWorldCups(action: String): Result<List<WorldCup>> = runCatching {
        api.getAllData(
            key = BuildConfig.API_KEY,
            action = action,
        )
    }

    suspend fun insertWorldCup(body: Map<String, Any>): Result<Response<ResponseBody>> = runCatching {
        api.insertWorldCup(body)
    }
}

