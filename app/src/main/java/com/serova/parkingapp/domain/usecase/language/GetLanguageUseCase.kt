package com.serova.parkingapp.domain.usecase.language

import com.serova.parkingapp.domain.model.settings.AppLanguage
import com.serova.parkingapp.domain.repository.LanguageRepository
import javax.inject.Inject

class GetLanguageUseCase @Inject constructor(
    private val repository: LanguageRepository
) {
    operator fun invoke(): AppLanguage {
        return repository.getCurrentLanguage()
    }
}