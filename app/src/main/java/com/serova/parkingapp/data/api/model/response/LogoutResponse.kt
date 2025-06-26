package com.serova.parkingapp.data.api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class LogoutResponse(
    val status: String,
    val requestId: String
)