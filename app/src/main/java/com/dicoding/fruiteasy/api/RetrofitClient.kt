package com.dicoding.fruiteasy.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://fruiteasy-be-nrw674jbdq-et.a.run.app/"
//    private const val BASE_URL = "http://127.0.0.1:5000/"
//    private const val BASE_URL = "http://192.168.1.19:3000/"


    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}