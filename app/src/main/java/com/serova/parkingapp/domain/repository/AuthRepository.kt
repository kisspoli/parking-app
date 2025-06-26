package com.serova.parkingapp.domain.repository

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<Unit>
    suspend fun refreshAuth(): Result<Unit>
}