package com.serova.parkingapp.domain.usecase.language

import com.serova.parkingapp.domain.model.settings.AppLanguage
import com.serova.parkingapp.domain.repository.LanguageRepository
import javax.inject.Inject

class SaveLanguageUseCase @Inject constructor(
    private val repository: LanguageRepository
) {
    suspend operator fun invoke(appLanguage: AppLanguage) {
        repository.saveLanguage(appLanguage)
    }
}