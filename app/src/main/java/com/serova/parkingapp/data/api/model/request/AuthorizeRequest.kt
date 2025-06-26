package com.serova.parkingapp.data.api.model.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizeRequest(
    val username: String,
    val password: String
)