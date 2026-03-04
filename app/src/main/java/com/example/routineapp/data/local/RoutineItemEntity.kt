package com.example.routineapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val name: String,
    val durationMinutes: Int? = null,
    val orderIndex: Int
)
