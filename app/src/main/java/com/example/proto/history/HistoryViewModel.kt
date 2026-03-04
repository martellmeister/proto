package com.example.proto.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = true,
    val month: HistoryMonthUiModel? = null,
    val selectedDayDetails: DayDetailsUiModel? = null
)

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var selectedMonth: YearMonth = YearMonth.now()

    init {
        loadMonth()
    }

    fun loadMonth() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val monthData = repository.getMonth(selectedMonth)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                month = monthData,
                selectedDayDetails = null
            )
        }
    }

    fun previousMonth() {
        selectedMonth = selectedMonth.minusMonths(1)
        loadMonth()
    }

    fun nextMonth() {
        selectedMonth = selectedMonth.plusMonths(1)
        loadMonth()
    }

    fun openDayDetails(day: LocalDate) {
        viewModelScope.launch {
            val details = repository.getDayDetails(day)
            _uiState.value = _uiState.value.copy(selectedDayDetails = details)
        }
    }

    fun closeDayDetails() {
        _uiState.value = _uiState.value.copy(selectedDayDetails = null)
    }
}
