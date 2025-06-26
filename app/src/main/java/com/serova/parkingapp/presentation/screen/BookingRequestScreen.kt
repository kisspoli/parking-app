package com.serova.parkingapp.presentation.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serova.parkingapp.R
import com.serova.parkingapp.presentation.ui.handler.AlertDialogsHandler
import com.serova.parkingapp.presentation.ui.handler.KeyboardEffectsHandler
import com.serova.parkingapp.presentation.ui.helper.euFormatted
import com.serova.parkingapp.presentation.ui.helper.toLocalDate
import com.serova.parkingapp.presentation.viewmodel.BookingRequestViewModel
import kotlinx.datetime.Clock.System
import kotlinx.datetime.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingRequestScreen(
    globalNavController: NavController,
    viewModel: BookingRequestViewModel = hiltViewModel()
) {
    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = System.now().toEpochMilliseconds(),
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = utcTimeMillis
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                return !calendar.before(today)
            }
        }
    )
    var carModel by remember { mutableStateOf("") }
    var stateNumber by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val areFieldsValid = dateState.selectedDateMillis != null
            && carModel.isNotBlank()
            && stateNumber.isValidRussianStateNumber()
    val imeInsets = WindowInsets.ime.asPaddingValues()

    KeyboardEffectsHandler(scrollState)
    HandleRequestDialogOpened(uiState, keyboardController, focusManager)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = if (imeInsets.calculateBottomPadding() > 80.dp) {
                    imeInsets.calculateBottomPadding() - 80.dp
                } else {
                    imeInsets.calculateBottomPadding()
                }
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        BookingContent(
            uiState = uiState,
            dateState = dateState,
            carModel = carModel,
            stateNumber = stateNumber,
            onCarModelChange = { carModel = it },
            onStateNumberChange = { stateNumber = it.uppercase().filter { it != ' ' } },
            focusManager = focusManager,
            scrollState = scrollState,
            keyboardController = keyboardController,
            areFieldsValid = areFieldsValid,
            onSubmit = {
                dateState.toLocalDate()?.let { date ->
                    viewModel.showRequestDialog(Triple(date, carModel, stateNumber))
                }
            }
        )

        RequestDialogHandler(
            uiState = uiState,
            onRequestDialogConfirmed = { viewModel.makeBooking(it.first, it.second, it.third) },
            onRequestDialogDismiss = { viewModel.hideRequestDialog() }
        )

        ClearFieldsHandler(
            viewModel = viewModel,
            uiState = uiState,
            setDefaultDate = { dateState.selectedDateMillis = System.now().toEpochMilliseconds() },
            setDefaultCarModel = { carModel = "" },
            setDefaultStateNumber = { stateNumber = "" }
        )

        AlertDialogsHandler(
            uiState = uiState,
            globalNavController = globalNavController,
            onDismiss = { viewModel.dismissAlert() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingContent(
    uiState: BookingRequestViewModel.UiState,
    dateState: DatePickerState,
    carModel: String,
    stateNumber: String,
    onCarModelChange: (String) -> Unit,
    onStateNumberChange: (String) -> Unit,
    focusManager: FocusManager,
    scrollState: ScrollState,
    keyboardController: SoftwareKeyboardController?,
    areFieldsValid: Boolean,
    onSubmit: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenTitle()

            RequestForm(
                dateState = dateState,
                carModel = carModel,
                stateNumber = stateNumber,
                onCarModelChange = onCarModelChange,
                onStateNumberChange = onStateNumberChange,
                focusManager = focusManager,
                scrollState = scrollState,
                keyboardController = keyboardController
            )
        }
        BookingButton(
            isLoading = uiState.isLoading,
            areFieldsValid = areFieldsValid,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            onClick = onSubmit
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
            text = stringResource(R.string.request_screen_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RequestForm(
    dateState: DatePickerState,
    carModel: String,
    stateNumber: String,
    onCarModelChange: (String) -> Unit,
    onStateNumberChange: (String) -> Unit,
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

        DateInputField(dateState = dateState)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = carModel,
            onValueChange = onCarModelChange,
            label = { Text(stringResource(R.string.common_car_model)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = stateNumber,
            onValueChange = onStateNumberChange,
            label = { Text(stringResource(R.string.common_state_number)) },
            isError = !stateNumber.isValidRussianStateNumber() && !stateNumber.isEmpty(),
            supportingText = {
                if (!stateNumber.isValidRussianStateNumber() && !stateNumber.isEmpty()) {
                    Text(
                        text = stringResource(R.string.common_state_number_validation_hint),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateInputField(
    dateState: DatePickerState
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val selectedDate = dateState.toLocalDate()
    OutlinedTextField(
        value = selectedDate?.euFormatted() ?: "",
        onValueChange = { },
        enabled = false,
        readOnly = true,
        label = { Text(stringResource(R.string.common_date)) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select Date"
            )
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = Color.Transparent,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledPrefixColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledSuffixColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    enabled = dateState.selectedDateMillis != null
                ) {
                    Text(stringResource(R.string.common_ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }
}

@Composable
private fun BookingButton(
    isLoading: Boolean,
    areFieldsValid: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading && areFieldsValid,
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
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = stringResource(R.string.common_book),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun RequestDialogHandler(
    uiState: BookingRequestViewModel.UiState,
    onRequestDialogConfirmed: (Triple<LocalDate, String, String>) -> Unit,
    onRequestDialogDismiss: () -> Unit
) {
    uiState.requestDialogState?.let { (date, carModel, stateNumber) ->
        AlertDialog(
            onDismissRequest = onRequestDialogDismiss,
            title = { Text(stringResource(R.string.common_confirm_booking_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.common_date) + ": " + date.euFormatted())
                    Text(stringResource(R.string.common_car_model) + ": " + carModel)
                    Text(stringResource(R.string.common_state_number) + ": " + stateNumber)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onRequestDialogConfirmed(Triple(date, carModel, stateNumber)) }
                ) {
                    Text(stringResource(R.string.common_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onRequestDialogDismiss
                ) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}

@Composable
private fun HandleRequestDialogOpened(
    uiState: BookingRequestViewModel.UiState,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    LaunchedEffect(uiState.requestDialogState) {
        if (uiState.requestDialogState != null) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClearFieldsHandler(
    viewModel: BookingRequestViewModel,
    uiState: BookingRequestViewModel.UiState,
    setDefaultDate: () -> Unit,
    setDefaultCarModel: () -> Unit,
    setDefaultStateNumber: () -> Unit
) {
    LaunchedEffect(uiState.shouldClearFields) {
        if (uiState.shouldClearFields) {
            setDefaultDate()
            setDefaultCarModel()
            setDefaultStateNumber()
            viewModel.onFieldsCleared()
        }
    }
}

private fun String.isValidRussianStateNumber(): Boolean {
    val regex = "^[ABEKMHOPCTYXАВЕКМНОРСТУХ]\\d{3}[ABEKMHOPCTYXАВЕКМНОРСТУХ]{2}\\d{2,3}$"
    return matches(Regex(regex))
}