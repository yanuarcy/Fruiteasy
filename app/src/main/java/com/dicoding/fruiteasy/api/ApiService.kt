package com.dicoding.fruiteasy.api

import com.dicoding.fruiteasy.model.ContactUsRequest
import com.dicoding.fruiteasy.model.FruitContent
import com.dicoding.fruiteasy.model.FruitSeason
import com.dicoding.fruiteasy.model.HistoryResponse
import com.dicoding.fruiteasy.model.LoginRequest
import com.dicoding.fruiteasy.model.LoginResponse
import com.dicoding.fruiteasy.model.Predict
import com.dicoding.fruiteasy.model.PredictHistoryRequest
import com.dicoding.fruiteasy.model.ReportBugRequest
import com.dicoding.fruiteasy.model.RequestResetPasswordLink
import com.dicoding.fruiteasy.model.ResetPasswordRequest
import com.dicoding.fruiteasy.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("/UserAuth/request-verify-email")
    fun requestVerifyEmail(@Body user: User): Call<Void>

    @POST("/UserAuth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("/UserAuth/reset-password/")
    fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Call<Void>

    @Multipart
    @POST("/Predict/upload")
    fun uploadImage(@Part file: MultipartBody.Part): Call<ResponseBody>

    @POST("Predict/history")
    fun postPredictHistory(@Body request: PredictHistoryRequest): Call<ResponseBody>

    @GET("/Predict/get-history")
    fun getHistory(@Query("userLocalid") userLocalid: String): Call<List<HistoryResponse>>

    @POST("/UserAuth/edit-profile")
    fun editProfile(@Body user: User): Call<Void>

    @POST("/Support/report-bug")
    fun submitReportBug(@Body reportBugRequest: ReportBugRequest): Call<Void>

    @POST("/ForgotPass/request-password-link")
    fun requestPasswordLink(@Body resetPasswordRequest: RequestResetPasswordLink): Call<Void>

    @POST("/Support/contact-us")
    fun submitContactUs(@Body contactUsRequest: ContactUsRequest): Call<Void>

    @GET("/GetContent/content-fruit/current-month")
    fun getFruitContentByCurrentMonth(): Call<List<FruitContent>>

    @GET("/GetContent/musim_buah")
    fun getFruitSeasonByMonth(@Query("bulan") month: Int): Call<List<FruitSeason>>
}