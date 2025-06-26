package com.serova.parkingapp.domain.usecase.theme

import com.serova.parkingapp.domain.model.settings.AppTheme
import com.serova.parkingapp.domain.repository.ThemeRepository
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    operator fun invoke(): AppTheme {
        return repository.getCurrentTheme()
    }
}