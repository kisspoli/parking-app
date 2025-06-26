package com.serova.parkingapp.domain.usecase.theme

import com.serova.parkingapp.domain.model.settings.AppTheme
import com.serova.parkingapp.domain.repository.ThemeRepository
import javax.inject.Inject

class SaveThemeUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    suspend operator fun invoke(theme: AppTheme) {
        repository.saveTheme(theme)
    }
}