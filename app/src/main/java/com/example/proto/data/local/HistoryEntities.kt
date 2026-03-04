package com.example.proto.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val isArchived: Boolean = false
)

@Entity(
    tableName = "routine_items",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("routineId")]
)
data class RoutineItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long,
    val title: String
)

@Entity(
    tableName = "item_completions",
    foreignKeys = [
        ForeignKey(
            entity = RoutineItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["itemId", "completionDate"], unique = true)]
)
data class ItemCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemId: Long,
    val completionDate: String
)

@Entity(
    tableName = "daily_summary",
    indices = [Index(value = ["summaryDate"], unique = true)]
)
data class DailySummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** ISO date in yyyy-MM-dd format */
    val summaryDate: String,
    /** value in range [0..100] */
    val completionPercent: Int
)
