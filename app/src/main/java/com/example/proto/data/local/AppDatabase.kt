package com.example.proto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proto.history.HistoryDao

@Database(
    entities = [
        RoutineEntity::class,
        RoutineItemEntity::class,
        ItemCompletionEntity::class,
        DailySummaryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
