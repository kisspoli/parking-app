package com.serova.parkingapp.domain.usecase.auth

import com.serova.parkingapp.domain.repository.AuthRepository
import javax.inject.Inject

class RefreshAuthUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshAuth()
    }
}