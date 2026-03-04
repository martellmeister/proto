package com.example.routineapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RoutineEntity::class, RoutineItemEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
}
