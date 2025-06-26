package com.serova.parkingapp.data.repository

import com.serova.parkingapp.data.api.PersonalDataApi
import com.serova.parkingapp.data.local.cache.personaldata.PersonalDataCache
import com.serova.parkingapp.domain.model.resource.Resource
import com.serova.parkingapp.domain.repository.PersonalDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PersonalDataRepositoryImpl @Inject constructor(
    private val personalDataApi: PersonalDataApi,
    private val cache: PersonalDataCache
) : PersonalDataRepository {

    override suspend fun getPersonalData(
        respectCache: Boolean,
        forceUpdate: Boolean
    ): Flow<Resource<String?>> = flow {
        val cachedFullName = cache.getPersonalData()
        try {
            if (respectCache && cachedFullName != null) {
                emit(Resource.Loading())
                emit(Resource.Success(cachedFullName))
            }

            if (forceUpdate || cachedFullName == null) {
                emit(Resource.Loading())
                val fullName = personalDataApi.getPersonalData().fullName
                cache.savePersonalData(fullName)
                emit(Resource.Success(fullName))
            }

        } catch (e: Exception) {
            emit(Resource.Loading())

            if (respectCache) {
                emit(Resource.Error(cachedFullName, e))
            } else {
                emit(Resource.Error(null, e))
            }
        }
    }
}