package com.serova.parkingapp.data.api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val status: String,
    val requestId: String
)