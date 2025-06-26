package com.serova.parkingapp.data.api.model.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizeByRefreshRequest(
    val refreshToken: String
)