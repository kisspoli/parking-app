package com.serova.parkingapp.domain.repository

interface LogoutRepository {
    suspend fun logout(): Result<Unit>
}