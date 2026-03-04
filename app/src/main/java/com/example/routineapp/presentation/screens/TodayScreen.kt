package com.example.routineapp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.routineapp.domain.model.RoutineChecklistItem
import com.example.routineapp.domain.model.RoutineWithItems
import com.example.routineapp.presentation.viewmodel.TodayUiState
import kotlin.math.roundToInt

@Composable
fun TodayScreen(
    uiState: TodayUiState,
    onToggleItem: (Long, Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.loading -> {
            Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
                Text(text = "Loading...")
            }
        }

        uiState.routines.isEmpty() -> {
            Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
                Text(text = "Нет активных рутин на сегодня")
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                items(uiState.routines, key = { it.routineId }) { routine ->
                    RoutineCard(
                        routine = routine,
                        checkedItemIds = uiState.checkedItemIdsByRoutine[routine.routineId] ?: emptySet(),
                        onToggleItem = onToggleItem
                    )
                }
            }
        }
    }
}

@Composable
private fun RoutineCard(
    routine: RoutineWithItems,
    checkedItemIds: Set<Long>,
    onToggleItem: (Long, Long, Boolean) -> Unit
) {
    val progress = if (routine.items.isEmpty()) {
        0
    } else {
        ((checkedItemIds.size.toFloat() / routine.items.size.toFloat()) * 100).roundToInt()
    }

    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = routine.title, style = MaterialTheme.typography.titleMedium)
                Text(text = "$progress%", style = MaterialTheme.typography.labelLarge)
            }

            routine.items.forEach { item ->
                RoutineItemRow(
                    routineId = routine.routineId,
                    item = item,
                    checked = checkedItemIds.contains(item.id),
                    onToggleItem = onToggleItem
                )
            }
        }
    }
}

@Composable
private fun RoutineItemRow(
    routineId: Long,
    item: RoutineChecklistItem,
    checked: Boolean,
    onToggleItem: (Long, Long, Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked -> onToggleItem(routineId, item.id, isChecked) }
        )

        Column(modifier = Modifier.padding(top = 10.dp)) {
            Text(text = item.name)
            item.durationMinutes?.let { minutes ->
                Text(
                    text = "$minutes мин",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
