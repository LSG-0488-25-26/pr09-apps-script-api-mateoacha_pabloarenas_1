package com.example.pr09_app.data.network

import com.example.pr09_app.data.model.WorldCup
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WorldCupApiService {
    @GET("exec")
    suspend fun getAllData(
        @Query("key") key: String,
        @Query("action") action: String = "listAll",
    ): List<WorldCup>

    @POST("exec")
    suspend fun insertWorldCup(
        @Body body: Map<String, Any>,
    ): Response<ResponseBody>
}

