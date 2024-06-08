package com.dicoding.fruiteasy.api

import com.dicoding.fruiteasy.model.LoginRequest
import com.dicoding.fruiteasy.model.LoginResponse
import com.dicoding.fruiteasy.model.ResetPasswordRequest
import com.dicoding.fruiteasy.model.User
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("/UserAuth/signup")
    fun registerUser(@Body user: User): Call<Void>

    @POST("/UserAuth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("/UserAuth/reset-password/")
    fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Call<Void>

    @Multipart
    @POST("/predict")
    fun uploadImage(@Part file: MultipartBody.Part): Call<String>
}