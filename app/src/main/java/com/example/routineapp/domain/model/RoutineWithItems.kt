package com.example.routineapp.domain.model

data class RoutineWithItems(
    val routineId: Long,
    val title: String,
    val items: List<RoutineChecklistItem>
)
