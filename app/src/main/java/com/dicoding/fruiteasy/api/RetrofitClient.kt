package com.dicoding.fruiteasy.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://fruiteasy-be-nrw674jbdq-et.a.run.app/"
//    private const val BASE_URL = "https://fruiteasy-model-rjljvagvsa-et.a.run.app/"
//    private const val BASE_URL = "http://127.0.0.1:5000/"
//    private const val BASE_URL = "http://192.168.1.19:3000/"


    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Increase the connection timeout
        .readTimeout(300, TimeUnit.SECONDS)    // Increase the read timeout
        .writeTimeout(60, TimeUnit.SECONDS)   // Increase the write timeout
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}