package com.example.routineapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface RoutineDao {
    @Transaction
    @Query("SELECT * FROM routines WHERE isActive = 1 ORDER BY id ASC")
    suspend fun getActiveRoutinesWithItems(): List<RoutineWithItemsEntity>

    @Insert
    suspend fun insertRoutine(routine: RoutineEntity): Long

    @Insert
    suspend fun insertRoutineItems(items: List<RoutineItemEntity>)

    @Query("SELECT COUNT(*) FROM routines")
    suspend fun routinesCount(): Int
}
