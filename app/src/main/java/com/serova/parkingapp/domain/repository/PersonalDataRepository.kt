package com.serova.parkingapp.domain.repository

import com.serova.parkingapp.domain.model.resource.Resource
import kotlinx.coroutines.flow.Flow

interface PersonalDataRepository {
    suspend fun getPersonalData(
        respectCache: Boolean,
        forceUpdate: Boolean
    ): Flow<Resource<String?>>
}