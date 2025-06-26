package com.serova.parkingapp.presentation.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.serova.parkingapp.MainActivity
import com.serova.parkingapp.R
import com.serova.parkingapp.domain.model.settings.AppLanguage
import com.serova.parkingapp.domain.model.settings.AppTheme
import com.serova.parkingapp.presentation.ui.handler.AlertDialogsHandler
import com.serova.parkingapp.presentation.ui.helper.LoadingContent
import com.serova.parkingapp.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    globalNavController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        SettingsContent(
            viewModel = viewModel,
            uiState = uiState,
            onLogout = { viewModel.viewModelScope.launch { viewModel.logout() } }
        )

        LanguageChangeHandler(viewModel = viewModel)

        AlertDialogsHandler(
            uiState = uiState,
            globalNavController = globalNavController,
            onDismiss = { viewModel.dismissAlert() }
        )
    }
}

@Composable
private fun SettingsContent(
    viewModel: SettingsViewModel,
    uiState: SettingsViewModel.UiState,
    onLogout: () -> Unit
) {
    val defaultSectionModifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenTitle()

            UserInfoSection(
                uiState = uiState,
                modifier = defaultSectionModifier
            )

            LanguageSection(
                currentLanguage = uiState.currentLanguage,
                onLanguageSelected = { viewModel.setLanguage(it) },
                modifier = defaultSectionModifier
            )

            ThemeSection(
                currentTheme = uiState.currentTheme,
                onThemeSelected = { viewModel.setTheme(it) },
                modifier = defaultSectionModifier
            )

            AppInfoSection(
                modifier = defaultSectionModifier
            )
        }

        LogoutButton(
            isLoggingOut = uiState.isLoggingOut,
            onLogout = onLogout,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun ScreenTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stringResource(R.string.settings_screen_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun UserInfoSection(
    uiState: SettingsViewModel.UiState,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.common_name),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LoadingContent(
            isLoading = uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        ) {
            Text(
                text = uiState.fullName ?: stringResource(R.string.common_loading_error),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LanguageSection(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.common_language),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LanguageSelector(
            currentLanguage = currentLanguage,
            onLanguageSelected = onLanguageSelected
        )
    }
}

@Composable
fun LanguageSelector(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Column(
            modifier = Modifier
                .clickable { expanded = true }
                .align(Alignment.CenterStart)
        ) {
            Row {
                Text(
                    text = when (currentLanguage) {
                        AppLanguage.RUSSIAN -> stringResource(R.string.common_language_russian)
                        AppLanguage.ENGLISH -> stringResource(R.string.common_language_english)
                        AppLanguage.SYSTEM -> stringResource(R.string.common_language_system)
                    }
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select language"
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AppLanguage.entries.forEach { language ->
                DropdownMenuItem(
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = when (language) {
                                AppLanguage.RUSSIAN -> stringResource(R.string.common_language_russian)
                                AppLanguage.ENGLISH -> stringResource(R.string.common_language_english)
                                AppLanguage.SYSTEM -> stringResource(R.string.common_language_system)
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ThemeSection(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.common_theme),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ThemeSelector(
            currentTheme = currentTheme,
            onThemeSelected = onThemeSelected
        )
    }
}

@Composable
fun ThemeSelector(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Column(
            modifier = Modifier
                .clickable { expanded = true }
                .align(Alignment.CenterStart)
        ) {
            Row {
                Text(
                    text = when (currentTheme) {
                        AppTheme.DARK -> stringResource(R.string.common_theme_dark)
                        AppTheme.LIGHT -> stringResource(R.string.common_theme_light)
                        AppTheme.SYSTEM -> stringResource(R.string.common_theme_system)
                    }
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select language"
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AppTheme.entries.forEach { theme ->
                DropdownMenuItem(
                    onClick = {
                        onThemeSelected(theme)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = when (theme) {
                                AppTheme.DARK -> stringResource(R.string.common_theme_dark)
                                AppTheme.LIGHT -> stringResource(R.string.common_theme_light)
                                AppTheme.SYSTEM -> stringResource(R.string.common_theme_system)
                            }
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun AppInfoSection(
    modifier: Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.common_about_app),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(stringResource(R.string.app_name))

        val packageManager = LocalContext.current.packageManager
        val packageName = LocalContext.current.packageName
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        Text(stringResource(R.string.common_app_version) + ": " + versionName)
    }
}

@Composable
private fun LogoutButton(
    isLoggingOut: Boolean,
    onLogout: () -> Unit,
    modifier: Modifier
) {
    Button(
        onClick = onLogout,
        enabled = !isLoggingOut,
        modifier = modifier.padding(horizontal = 24.dp),
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
        if (isLoggingOut) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = stringResource(R.string.common_logout),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun LanguageChangeHandler(
    viewModel: SettingsViewModel,
    context: Context = LocalContext.current
) {
    LaunchedEffect(Unit) {
        viewModel.recreateActivity.collect {
            (context as Activity).apply {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                @Suppress("DEPRECATION")
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }
}