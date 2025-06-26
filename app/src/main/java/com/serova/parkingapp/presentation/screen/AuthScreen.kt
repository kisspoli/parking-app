package com.serova.parkingapp.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.serova.parkingapp.R
import com.serova.parkingapp.presentation.ui.handler.KeyboardEffectsHandler
import com.serova.parkingapp.presentation.viewmodel.AuthViewModel
import com.serova.parkingapp.presentation.viewmodel.data.AlertData
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val areFieldsFilled = username.isNotBlank() && password.isNotBlank()
    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues()

    KeyboardEffectsHandler(scrollState)
    HandleAuthSuccess(uiState, keyboardController, focusManager, onLoginSuccess)

    when {
        uiState.isAuthenticated || (uiState.isLoading && !areFieldsFilled) -> {
            LoadingIndicator()
        }

        else -> {
            AuthContent(
                username = username,
                password = password,
                onUsernameChange = { username = it },
                onPasswordChange = { password = it },
                areFieldsFilled = areFieldsFilled,
                isLoading = uiState.isLoading,
                scrollState = scrollState,
                focusManager = focusManager,
                keyboardController = keyboardController,
                navigationBarsPadding = navigationBarsPadding,
                onLoginClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    coroutineScope.launch { viewModel.login(username, password) }
                },
                alertData = uiState.alertData,
                onDismissError = { viewModel.dismissAlert() }
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    AnimatedVisibility(
        visible = true,
        exit = fadeOut() + scaleOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AuthContent(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    areFieldsFilled: Boolean,
    isLoading: Boolean,
    scrollState: ScrollState,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    navigationBarsPadding: PaddingValues,
    onLoginClick: () -> Unit,
    alertData: AlertData?,
    onDismissError: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        AuthForm(
            username = username,
            password = password,
            onUsernameChange = onUsernameChange,
            onPasswordChange = onPasswordChange,
            focusManager = focusManager,
            scrollState = scrollState,
            keyboardController = keyboardController
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = navigationBarsPadding.calculateBottomPadding() + 24.dp)
        ) {
            LoginButton(
                isLoading = isLoading,
                areFieldsFilled = areFieldsFilled,
                onLoginClick = onLoginClick
            )
        }

        ErrorAlert(
            alertData = alertData,
            onDismissError = onDismissError
        )
    }
}

@Composable
private fun AuthForm(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    focusManager: FocusManager,
    scrollState: ScrollState,
    keyboardController: SoftwareKeyboardController?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp)
            .padding(bottom = 88.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text(stringResource(R.string.common_username)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.common_password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    areFieldsFilled: Boolean,
    onLoginClick: () -> Unit
) {
    Button(
        onClick = onLoginClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = areFieldsFilled && !isLoading,
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(vertical = 18.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = stringResource(R.string.common_login),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun ErrorAlert(
    alertData: AlertData?,
    onDismissError: () -> Unit
) {
    alertData?.let {
        AlertDialog(
            onDismissRequest = onDismissError,
            title = { Text(it.title) },
            text = { Text(it.message) },
            confirmButton = {
                TextButton(onClick = onDismissError) {
                    Text(stringResource(R.string.common_got_it))
                }
            }
        )
    }
}

@Composable
private fun HandleAuthSuccess(
    uiState: AuthViewModel.UiState,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
    onLoginSuccess: () -> Unit
) {
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            keyboardController?.hide()
            focusManager.clearFocus()
            onLoginSuccess()
        }
    }
}