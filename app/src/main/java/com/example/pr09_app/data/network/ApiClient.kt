package com.example.pr09_app.data.network

import com.example.pr09_app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton de Retrofit para conexión con la API de Apps Script
 * 
 * Configuración:
 * - Base URL desde BuildConfig.BASE_URL (secrets.properties)
 * - OkHttpClient con logging interceptor (DEBUG friendly)
 * - GSON converter para serialización JSON
 */
object ApiClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        // Evita que la UI se quede “cargando” demasiado tiempo cuando falla la red.
        .retryOnConnectionFailure(false)
        .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .callTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * Instancia del servicio Retrofit para consumir endpoints
     */
    val service: WorldCupApiService = retrofit.create(WorldCupApiService::class.java)
}

