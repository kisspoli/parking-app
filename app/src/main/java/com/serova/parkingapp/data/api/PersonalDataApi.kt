package com.serova.parkingapp.data.api

import com.serova.parkingapp.data.api.model.response.PersonalDataResponse
import retrofit2.http.GET

interface PersonalDataApi {
    @GET("parking/personal_data")
    suspend fun getPersonalData(): PersonalDataResponse
}