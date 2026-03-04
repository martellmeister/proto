package com.example.routineapp.data.repository

import com.example.routineapp.data.local.RoutineDao
import com.example.routineapp.data.local.RoutineEntity
import com.example.routineapp.data.local.RoutineItemEntity
import com.example.routineapp.domain.model.RoutineChecklistItem
import com.example.routineapp.domain.model.RoutineWithItems
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineRepository @Inject constructor(
    private val routineDao: RoutineDao
) {
    suspend fun ensureDemoData() {
        if (routineDao.routinesCount() > 0) return

        val morningRoutineId = routineDao.insertRoutine(
            RoutineEntity(title = "Утренний протокол", isActive = true)
        )
        routineDao.insertRoutineItems(
            listOf(
                RoutineItemEntity(routineId = morningRoutineId, name = "Зарядка", durationMinutes = 10, orderIndex = 0),
                RoutineItemEntity(routineId = morningRoutineId, name = "Стакан воды", orderIndex = 1),
                RoutineItemEntity(routineId = morningRoutineId, name = "Контрастный душ", orderIndex = 2),
                RoutineItemEntity(routineId = morningRoutineId, name = "Дыхание", orderIndex = 3)
            )
        )

        val eveningRoutineId = routineDao.insertRoutine(
            RoutineEntity(title = "Вечерний ритуал", isActive = true)
        )
        routineDao.insertRoutineItems(
            listOf(
                RoutineItemEntity(routineId = eveningRoutineId, name = "Растяжка", durationMinutes = 5, orderIndex = 0),
                RoutineItemEntity(routineId = eveningRoutineId, name = "План на завтра", orderIndex = 1)
            )
        )
    }

    suspend fun getActiveRoutines(): List<RoutineWithItems> {
        return routineDao.getActiveRoutinesWithItems().map { routineWithItems ->
            RoutineWithItems(
                routineId = routineWithItems.routine.id,
                title = routineWithItems.routine.title,
                items = routineWithItems.items
                    .sortedBy { it.orderIndex }
                    .map {
                        RoutineChecklistItem(
                            id = it.id,
                            name = it.name,
                            durationMinutes = it.durationMinutes
                        )
                    }
            )
        }
    }
}
