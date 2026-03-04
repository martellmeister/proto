package com.example.routineapp.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class RoutineWithItemsEntity(
    @Embedded val routine: RoutineEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "routineId",
        entity = RoutineItemEntity::class
    )
    val items: List<RoutineItemEntity>
)
