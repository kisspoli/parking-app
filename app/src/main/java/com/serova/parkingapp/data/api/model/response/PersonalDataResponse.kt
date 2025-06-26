package com.serova.parkingapp.data.api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PersonalDataResponse(
    val fullName: String,
    val status: String,
    val requestId: String
)