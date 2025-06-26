package com.serova.parkingapp.data.api.model.response

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class AuthorizeByRefreshResponse(
    var accessToken: String,
    val refreshToken: String,
    @Contextual val validUntil: Instant,
    val status: String,
    val requestId: String
)