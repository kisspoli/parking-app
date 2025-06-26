package com.serova.parkingapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.serova.parkingapp.domain.model.settings.AppTheme
import com.serova.parkingapp.domain.usecase.theme.GetThemeFlowUseCase
import com.serova.parkingapp.presentation.navigation.Navigation
import com.serova.parkingapp.presentation.ui.theme.ParkingAppTheme

@Composable
fun ParkingApp(
    appTheme: AppTheme,
    getThemeFlowUseCase: GetThemeFlowUseCase
) {
    ParkingAppTheme(
        appTheme = appTheme,
        getThemeFlowUseCase = getThemeFlowUseCase
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Navigation()
        }
    }
}