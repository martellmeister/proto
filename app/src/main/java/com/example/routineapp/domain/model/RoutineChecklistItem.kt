package com.example.routineapp.domain.model

data class RoutineChecklistItem(
    val id: Long,
    val name: String,
    val durationMinutes: Int?
)
