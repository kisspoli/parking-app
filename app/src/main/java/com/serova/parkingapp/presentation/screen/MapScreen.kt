package com.serova.parkingapp.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serova.parkingapp.R
import com.serova.parkingapp.domain.model.bookingdetails.BookingDetails
import com.serova.parkingapp.presentation.ui.handler.AlertDialogsHandler
import com.serova.parkingapp.presentation.ui.helper.LoadingContent
import com.serova.parkingapp.presentation.ui.helper.euFormatted
import com.serova.parkingapp.presentation.ui.helper.toLocalDate
import com.serova.parkingapp.presentation.viewmodel.MapViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    globalNavController: NavController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedSpot by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var selectedSpotDetails by remember { mutableStateOf<BookingDetails?>(null) }

    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds(),
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
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        MapContent(
            uiState = uiState,
            dateState = dateState,
            onDateSelected = { viewModel.loadMap(it) },
            onSpotSelected = { row, column ->
                selectedSpot = Pair(row, column)
                selectedSpotDetails = uiState.bookingsDetails.find {
                    it.row == row && it.column == column
                }
                showBottomSheet = true
            },
            showBottomSheet = showBottomSheet,
            onDismissBottomSheetRequest = { showBottomSheet = false },
            sheetState = sheetState,
            selectedSpot = selectedSpot,
            selectedSpotDetails = selectedSpotDetails
        )

        HandleOpenScreen(
            viewModel = viewModel,
            dateState = dateState
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
private fun MapContent(
    uiState: MapViewModel.UiState,
    dateState: DatePickerState,
    onDateSelected: (LocalDate) -> Unit,
    onSpotSelected: (Int, Int) -> Unit,
    showBottomSheet: Boolean,
    onDismissBottomSheetRequest: () -> Unit,
    sheetState: SheetState,
    selectedSpot: Pair<Int, Int>?,
    selectedSpotDetails: BookingDetails?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenTitle()

            DatePickerWidget(
                dateState = dateState,
                onDateSelected = onDateSelected
            )

            Map(
                uiState = uiState,
                onSpotSelected = onSpotSelected
            )

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { onDismissBottomSheetRequest() },
                    sheetState = sheetState,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    ParkingSpotDetailsSheet(
                        spotDetails = selectedSpotDetails,
                        spotLocation = selectedSpot
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenTitle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stringResource(R.string.map_screen_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerWidget(
    dateState: DatePickerState,
    onDateSelected: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val selectedDate = dateState.toLocalDate()
    Button(
        onClick = { showDatePicker = true },
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text(text = selectedDate?.euFormatted() ?: stringResource(R.string.common_date))
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        dateState.toLocalDate()?.let { onDateSelected(it) }
                    },
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
private fun Map(
    uiState: MapViewModel.UiState,
    onSpotSelected: (Int, Int) -> Unit
) {
    val rows = 3
    val columns = 8

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(rows) { rowIndex ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(columns) { columnIndex ->
                    ParkingSpotView(
                        uiState = uiState,
                        spotInfo = uiState.bookingsDetails.find {
                            it.row == rowIndex + 1 && it.column == columnIndex + 1
                        },
                        rowIndex = rowIndex,
                        columnIndex = columnIndex,
                        onSpotSelected = onSpotSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun ParkingSpotView(
    uiState: MapViewModel.UiState,
    spotInfo: BookingDetails? = null,
    rowIndex: Int,
    columnIndex: Int,
    onSpotSelected: (Int, Int) -> Unit
) {
    val backgroundColor = when {
        spotInfo == null || !spotInfo.isReserved -> MaterialTheme.colorScheme.primary
        spotInfo.isReservedByMe -> MaterialTheme.colorScheme.secondary
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(width = 40.dp, height = 80.dp)
            .background(backgroundColor, shape = RoundedCornerShape(4.dp))
            .border(2.dp, backgroundColor, shape = RoundedCornerShape(4.dp))
            .clickable { onSpotSelected(rowIndex + 1, columnIndex + 1) }
    ) {
        LoadingContent(
            isLoading = uiState.isLoading || uiState.isError,
            contentAlignment = Alignment.Center
        ) {
            spotInfo?.let {
                Text(
                    text = "${it.row}-${it.column}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            } ?: Text(
                text = "${rowIndex + 1}-${columnIndex + 1}",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HandleOpenScreen(
    viewModel: MapViewModel,
    dateState: DatePickerState
) {
    LaunchedEffect(Unit) {
        dateState.toLocalDate()?.let { viewModel.loadMap(it) }
    }
}

@Composable
fun ParkingSpotDetailsSheet(
    spotDetails: BookingDetails?,
    spotLocation: Pair<Int, Int>?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        spotLocation?.let { (row, column) ->
            Text(
                text = stringResource(R.string.common_row) + ": $row, "
                        + stringResource(R.string.common_place).lowercase() + ": $column",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Text(
            text = if (spotDetails != null && spotDetails.isReserved) {
                if (spotDetails.isReservedByMe) {
                    stringResource(R.string.common_your_place)
                } else {
                    stringResource(R.string.common_reserved)
                }
            } else {
                stringResource(R.string.common_free)
            },
            color = when {
                spotDetails == null || !spotDetails.isReserved -> Color.Green
                spotDetails.isReservedByMe -> MaterialTheme.colorScheme.primary
                else -> Color.Red
            },
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            spotDetails?.let { details ->
                if (!details.reservedBy.isNullOrEmpty()) {
                    DetailRow(
                        stringResource(R.string.common_is_booked_by) + ":",
                        details.reservedBy
                    )
                }
                if (!details.carModel.isNullOrEmpty()) {
                    DetailRow(stringResource(R.string.common_car_model) + ":", details.carModel)
                }
                if (!details.stateNumber.isNullOrEmpty()) {
                    DetailRow(
                        stringResource(R.string.common_state_number) + ":",
                        details.stateNumber
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        )
    }
}