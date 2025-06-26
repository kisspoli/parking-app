package com.serova.parkingapp.domain.model.exception

import com.serova.parkingapp.data.api.model.response.ErrorResponse
import java.io.IOException

data class ApiException(
    val error: ErrorResponse,
    override val cause: Throwable? = null
) : IOException(
    "${error.message} (requestId: ${error.requestId})",
    cause
)