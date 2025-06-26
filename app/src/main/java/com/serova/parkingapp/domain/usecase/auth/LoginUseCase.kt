package com.serova.parkingapp.domain.usecase.auth

import com.serova.parkingapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(login: String, password: String): Result<Unit> {
        return repository.login(login, password)
    }
}