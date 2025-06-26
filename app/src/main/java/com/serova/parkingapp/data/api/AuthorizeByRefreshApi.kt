package com.serova.parkingapp.data.api

import com.serova.parkingapp.data.api.model.request.AuthorizeByRefreshRequest
import com.serova.parkingapp.data.api.model.response.AuthorizeByRefreshResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthorizeByRefreshApi {
    @POST("parking/auth/authorize/by_refresh")
    suspend fun login(
        @Body request: AuthorizeByRefreshRequest
    ): AuthorizeByRefreshResponse
}