package com.serova.parkingapp.data.api

import com.serova.parkingapp.data.api.model.response.LogoutResponse
import retrofit2.http.POST

interface LogoutApi {
    @POST("parking/auth/logout")
    suspend fun logout(): LogoutResponse
}