package com.serova.parkingapp.presentation.viewmodel.state

import com.serova.parkingapp.presentation.viewmodel.data.AlertData

abstract class CommonUiState {
    abstract val isLoading: Boolean
    abstract val alertData: AlertData?
    abstract val shouldLogout: Boolean
}
