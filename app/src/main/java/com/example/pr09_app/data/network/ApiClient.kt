package com.example.pr09_app.data.network

import com.example.pr09_app.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val apiKeyInterceptor = Interceptor { chain ->
        val request = chain.request()
        val url: HttpUrl = request.url.newBuilder()
            .addQueryParameter("apiKey", BuildConfig.API_KEY)
            .build()
        chain.proceed(request.newBuilder().url(url).build())
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: ApiService = retrofit.create(ApiService::class.java)
}

