package com.example.proto.history

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class HistoryRepository(
    private val historyDao: HistoryDao
) {

    suspend fun getMonth(month: YearMonth): HistoryMonthUiModel {
        val start = month.atDay(1)
        val end = month.atEndOfMonth()
        val summaries = historyDao.getDailySummariesForRange(
            monthStart = start.format(DateTimeFormatter.ISO_DATE),
            monthEnd = end.format(DateTimeFormatter.ISO_DATE)
        ).associate { row -> row.summaryDate to row.completionPercent.coerceIn(0, 100) }

        val leading = (start.dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7
        val dayCells = mutableListOf<CalendarDayCellUiModel>()

        repeat(leading) { dayCells.add(CalendarDayCellUiModel(date = null, percent = null)) }
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            val key = date.format(DateTimeFormatter.ISO_DATE)
            dayCells.add(CalendarDayCellUiModel(date = date, percent = summaries[key]))
        }
        while (dayCells.size % 7 != 0) {
            dayCells.add(CalendarDayCellUiModel(date = null, percent = null))
        }

        return HistoryMonthUiModel(month = month, cells = dayCells)
    }

    suspend fun getDayDetails(date: LocalDate): DayDetailsUiModel {
        val dayKey = date.format(DateTimeFormatter.ISO_DATE)
        val summary = historyDao.getDailySummary(dayKey)
        val routines = historyDao.getRoutineDetailsForDay(dayKey).map {
            HistoryRoutineUiModel(
                routineId = it.routineId,
                routineTitle = it.routineTitle,
                completedItems = it.completedItems,
                totalItems = it.totalItems,
                checkedItems = it.checkedItemsCsv
                    ?.split("||")
                    ?.filter { title -> title.isNotBlank() }
                    .orEmpty()
            )
        }

        return DayDetailsUiModel(
            date = date,
            completionPercent = summary?.completionPercent?.coerceIn(0, 100),
            routines = routines
        )
    }
}
