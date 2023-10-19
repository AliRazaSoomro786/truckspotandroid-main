package com.truckspot.api

import com.google.gson.JsonElement
import com.truckspot.models.CertifyModelItem
import com.truckspot.models.GetLogsResponse
import com.truckspot.models.LogResponse
import com.truckspot.models.LoginResponse
import com.truckspot.request.AddLogRequest
import com.truckspot.request.AddLogRequestunauth
import com.truckspot.request.LoginRequest
import com.truckspot.request.updateLogRequest
import retrofit2.Response
import retrofit2.http.*

interface TruckSpotAPI {

    @POST("api/v1/login")
    suspend fun login(@Body loginRequest: LoginRequest):Response<LoginResponse>

    @POST("api/v1/addLog")
    suspend fun addLog(@Body addLogRequest: AddLogRequest?):Response<LogResponse>

    @POST("api/v1/addLogUnauth")
    suspend fun addLogUnauth(@Body addLogRequest: AddLogRequestunauth): Response<LogResponse>
    @GET("api/v1/getLogs") // Replace with the correct endpoint path
    suspend fun getLogs(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("days") days: Int
    ): Response<GetLogsResponse>

    @GET("api/v1/get_certified")
    suspend fun getDates(): Response<List<CertifyModelItem>>

    @PUT("api/v1/updatelog")
    suspend fun updateLog(@Body updateLogRequest: updateLogRequest):Response<LogResponse>

    @PUT("api/v1/updatelog")
    suspend fun updateLogbyuser(@Body updateLogRequest: updateLogRequest):Response<LogResponse>

    @PUT("api/v1/updated_certified")
    suspend fun updatedCertified(@Body updateCertifiedRequest: CertifyModelItem):Response<CertifyModelItem>



}