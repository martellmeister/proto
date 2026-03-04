package com.example.routineapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.routineapp.data.repository.RoutineRepository
import com.example.routineapp.domain.model.RoutineWithItems
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TodayUiState(
    val loading: Boolean = true,
    val routines: List<RoutineWithItems> = emptyList(),
    val checkedItemIdsByRoutine: Map<Long, Set<Long>> = emptyMap()
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val repository: RoutineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureDemoData()
            _uiState.value = TodayUiState(
                loading = false,
                routines = repository.getActiveRoutines()
            )
        }
    }

    fun toggleItem(routineId: Long, itemId: Long, checked: Boolean) {
        val currentMap = _uiState.value.checkedItemIdsByRoutine.toMutableMap()
        val currentSet = (currentMap[routineId] ?: emptySet()).toMutableSet()

        if (checked) currentSet += itemId else currentSet -= itemId
        currentMap[routineId] = currentSet

        _uiState.value = _uiState.value.copy(checkedItemIdsByRoutine = currentMap)
    }
}
