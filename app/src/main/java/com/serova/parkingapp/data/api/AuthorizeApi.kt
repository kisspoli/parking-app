package com.serova.parkingapp.data.api

import com.serova.parkingapp.data.api.model.request.AuthorizeRequest
import com.serova.parkingapp.data.api.model.response.AuthorizeResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthorizeApi {
    @POST("parking/auth/authorize")
    suspend fun login(
        @Body request: AuthorizeRequest
    ): AuthorizeResponse
}