package com.serova.parkingapp.domain.usecase.personaldata

import com.serova.parkingapp.domain.model.resource.Resource
import com.serova.parkingapp.domain.repository.PersonalDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PersonalDataUseCase @Inject constructor(
    private val repository: PersonalDataRepository
) {
    suspend operator fun invoke(
        respectCache: Boolean = true,
        forceUpdate: Boolean = true
    ): Flow<Resource<String?>> {
        return repository.getPersonalData(respectCache, forceUpdate)
    }
}