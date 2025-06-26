package com.serova.parkingapp.presentation.ui.handler

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.serova.parkingapp.R
import com.serova.parkingapp.presentation.navigation.Screen
import com.serova.parkingapp.presentation.viewmodel.data.AlertData
import com.serova.parkingapp.presentation.viewmodel.state.CommonUiState

@Composable
fun AlertDialogsHandler(
    uiState: CommonUiState,
    globalNavController: NavController,
    onDismiss: () -> Unit
) {
    when {
        uiState.alertData != null && uiState.shouldLogout -> {
            LogoutAlert(
                alertData = uiState.alertData!!,
                onDismiss = { globalNavController.navigate(Screen.Auth.route) }
            )
        }

        uiState.shouldLogout -> {
            LaunchedEffect(Unit) {
                globalNavController.navigate(Screen.Auth.route)
            }
        }

        uiState.alertData != null -> {
            ErrorAlertDialog(
                alertData = uiState.alertData!!,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun LogoutAlert(
    alertData: AlertData,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(alertData.title) },
        text = { Text(alertData.message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_got_it))
            }
        }
    )
}

@Composable
private fun ErrorAlertDialog(
    alertData: AlertData,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(alertData.title) },
        text = { Text(alertData.message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_got_it))
            }
        }
    )
}