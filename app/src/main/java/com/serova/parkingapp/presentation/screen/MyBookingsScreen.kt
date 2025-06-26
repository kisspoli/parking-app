package com.serova.parkingapp.presentation.screen

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.serova.parkingapp.R
import com.serova.parkingapp.domain.model.booking.Booking
import com.serova.parkingapp.presentation.ui.handler.AlertDialogsHandler
import com.serova.parkingapp.presentation.ui.helper.euFormatted
import com.serova.parkingapp.presentation.viewmodel.MyBookingsViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun MyBookingsScreen(
    globalNavController: NavController,
    viewModel: MyBookingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        MyBookingsContent(
            uiState = uiState,
            onRefresh = viewModel::refreshData,
            onShowDeletionDialog = { viewModel.showDeletionDialog(it) }
        )

        DeletionDialogHandler(
            uiState = uiState,
            onDeleteConfirmed = { viewModel.deleteBooking(it) },
            onDeletionDialogDismiss = { viewModel.hideDeletionDialog() }
        )

        AlertDialogsHandler(
            uiState = uiState,
            globalNavController = globalNavController,
            onDismiss = { viewModel.dismissAlert() }
        )
    }
}

@Composable
private fun MyBookingsContent(
    uiState: MyBookingsViewModel.UiState,
    onRefresh: () -> Unit,
    onShowDeletionDialog: (Booking) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0) { 2 }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        val tabs = listOf(
            stringResource(R.string.common_confirmed),
            stringResource(R.string.common_unconfirmed)
        )

        ScreenTitle()

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> BookingsList(
                        bookings = uiState.bookings.filter { it.isConfirmed },
                        uiState = uiState,
                        onRefresh = onRefresh,
                        onShowDeletionDialog = onShowDeletionDialog
                    )

                    1 -> BookingsList(
                        bookings = uiState.bookings.filter { !it.isConfirmed },
                        uiState = uiState,
                        onRefresh = onRefresh,
                        onShowDeletionDialog = onShowDeletionDialog
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenTitle() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = stringResource(R.string.my_bookings_screen_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingsList(
    bookings: List<Booking>,
    uiState: MyBookingsViewModel.UiState,
    onRefresh: () -> Unit,
    onShowDeletionDialog: (Booking) -> Unit
) {
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
        isRefreshing = uiState.isLoading,
        onRefresh = onRefresh,
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = uiState.isLoading,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                state = state
            )
        },
    ) {
        when {
            bookings.isEmpty() -> EmptyState()
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                items(
                    items = bookings,
                    key = { it.id }
                ) { booking ->
                    SwipeableBookingItem(
                        booking = booking,
                        onDelete = { onShowDeletionDialog(booking) },
                        uiState = uiState
                    )
                }
            }
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun SwipeableBookingItem(
    booking: Booking,
    onDelete: () -> Unit,
    uiState: MyBookingsViewModel.UiState
) {
    val configuration = LocalConfiguration.current
    val screenWidth = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }

    val maxSwipePercentage = 0.4f
    val maxOffset = screenWidth * maxSwipePercentage
    val maxOffsetDp = with(LocalDensity.current) { maxOffset.toDp() }

    val animatable = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val shape = MaterialTheme.shapes.medium
    val backgroundShape = RoundedCornerShape(
        topEnd = shape.topEnd,
        bottomEnd = shape.bottomEnd,
        topStart = CornerSize(0.dp),
        bottomStart = CornerSize(0.dp)
    )

    LaunchedEffect(uiState.deletionDialogState) {
        if (uiState.deletionDialogState == null) {
            animatable.animateTo(0f, animationSpec = tween(durationMillis = 300))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(maxOffsetDp + 8.dp)
                .background(Color.Red, backgroundShape)
                .clip(backgroundShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Booking Deletion",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        BookingItem(
            booking = booking,
            modifier = Modifier
                .offset { IntOffset(x = animatable.value.roundToInt(), y = 0) }
                .nestedScroll(horizontalScrollConnection)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val newOffset = (animatable.value + delta).coerceIn(-maxOffset, 0f)
                        coroutineScope.launch {
                            animatable.snapTo(newOffset)
                        }
                    },
                    onDragStopped = { velocity ->
                        coroutineScope.launch {
                            if (animatable.value < -maxOffset * 0.5f) {
                                animatable.animateTo(
                                    targetValue = -maxOffset,
                                    animationSpec = tween(durationMillis = 150)
                                )
                                onDelete()
                            } else {
                                animatable.animateTo(
                                    targetValue = 0f,
                                    animationSpec = tween(durationMillis = 300)
                                )
                            }
                        }
                    }
                )
        )
    }
}

@Composable
private fun BookingItem(
    booking: Booking,
    modifier: Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.common_date) + ": ${booking.date.euFormatted()}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (booking.row != null && booking.column != null && booking.isConfirmed) {
                Text(
                    text = stringResource(R.string.common_row) + ": ${booking.row}, "
                            + stringResource(R.string.common_place) + ": ${booking.column}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = "${booking.carModel}, ${booking.stateNumber}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (booking.isConfirmed) {
                    stringResource(R.string.common_confirmed)
                } else {
                    stringResource(R.string.common_awaiting_confirmation)
                },
                color = if (booking.isConfirmed) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.common_no_bookings),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.common_pull_to_refresh),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DeletionDialogHandler(
    uiState: MyBookingsViewModel.UiState,
    onDeleteConfirmed: (Booking) -> Unit,
    onDeletionDialogDismiss: () -> Unit
) {
    uiState.deletionDialogState?.let { booking ->
        AlertDialog(
            onDismissRequest = onDeletionDialogDismiss,
            title = { Text(stringResource(R.string.common_delete_booking_confirmation_title)) },
            text = { Text(stringResource(R.string.common_delete_booking_confirmation_description)) },
            confirmButton = {
                TextButton(
                    onClick = { onDeleteConfirmed(booking) }
                ) {
                    Text(stringResource(R.string.common_confirm), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDeletionDialogDismiss
                ) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }
}

private val horizontalScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        return if (abs(available.x) > abs(available.y)) {
            Offset(available.x, 0f)
        } else {
            Offset.Zero
        }
    }
}