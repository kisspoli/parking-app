package com.serova.parkingapp.domain.usecase.language

import com.serova.parkingapp.domain.model.settings.AppLanguage
import com.serova.parkingapp.domain.repository.LanguageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLanguageFlowUseCase @Inject constructor(
    private val repository: LanguageRepository
) {
    operator fun invoke(): Flow<AppLanguage> {
        return repository.getCurrentLanguageAsFlow()
    }
}