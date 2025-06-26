package com.serova.parkingapp.domain.model.resource

sealed class Resource<out T> {
    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val data: T?, val error: Exception) : Resource<T>()
}