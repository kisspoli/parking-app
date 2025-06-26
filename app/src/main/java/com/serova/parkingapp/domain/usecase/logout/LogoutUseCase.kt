package com.serova.parkingapp.domain.usecase.logout

import com.serova.parkingapp.domain.repository.LogoutRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private var logoutRepository: LogoutRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return logoutRepository.logout()
    }
}