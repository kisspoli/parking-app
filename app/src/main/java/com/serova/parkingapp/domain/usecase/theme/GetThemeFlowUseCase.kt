package com.serova.parkingapp.domain.usecase.theme

import com.serova.parkingapp.domain.model.settings.AppTheme
import com.serova.parkingapp.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeFlowUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    operator fun invoke(): Flow<AppTheme> {
        return repository.getCurrentThemeAsFlow()
    }
}