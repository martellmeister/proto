package com.example.proto.history

import androidx.room.Dao
import androidx.room.Query

@Dao
interface HistoryDao {

    @Query(
        """
        SELECT summaryDate, completionPercent
        FROM daily_summary
        WHERE summaryDate >= :monthStart
          AND summaryDate <= :monthEnd
        """
    )
    suspend fun getDailySummariesForRange(
        monthStart: String,
        monthEnd: String
    ): List<DailySummaryRow>

    @Query(
        """
        SELECT summaryDate, completionPercent
        FROM daily_summary
        WHERE summaryDate = :day
        LIMIT 1
        """
    )
    suspend fun getDailySummary(day: String): DailySummaryRow?

    @Query(
        """
        SELECT
            r.id as routineId,
            r.title as routineTitle,
            SUM(CASE WHEN ic.itemId IS NOT NULL THEN 1 ELSE 0 END) as completedItems,
            COUNT(ri.id) as totalItems,
            GROUP_CONCAT(CASE WHEN ic.itemId IS NOT NULL THEN ri.title ELSE NULL END, '||') as checkedItemsCsv
        FROM routines r
        LEFT JOIN routine_items ri ON ri.routineId = r.id
        LEFT JOIN item_completions ic
            ON ic.itemId = ri.id
           AND ic.completionDate = :day
        WHERE r.isArchived = 0
        GROUP BY r.id, r.title
        ORDER BY r.title COLLATE NOCASE
        """
    )
    suspend fun getRoutineDetailsForDay(day: String): List<RoutineDayDetailRow>
}
