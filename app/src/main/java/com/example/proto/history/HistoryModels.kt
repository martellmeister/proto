package com.example.proto.history

import java.time.LocalDate
import java.time.YearMonth

data class HistoryMonthUiModel(
    val month: YearMonth,
    val cells: List<CalendarDayCellUiModel>
)

data class CalendarDayCellUiModel(
    val date: LocalDate?,
    val percent: Int?
)

data class DayDetailsUiModel(
    val date: LocalDate,
    val completionPercent: Int?,
    val routines: List<HistoryRoutineUiModel>
)

data class HistoryRoutineUiModel(
    val routineId: Long,
    val routineTitle: String,
    val completedItems: Int,
    val totalItems: Int,
    val checkedItems: List<String>
)

data class DailySummaryRow(
    val summaryDate: String,
    val completionPercent: Int
)

data class RoutineDayDetailRow(
    val routineId: Long,
    val routineTitle: String,
    val completedItems: Int,
    val totalItems: Int,
    val checkedItemsCsv: String?
)
