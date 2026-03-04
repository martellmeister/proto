package com.example.proto.history

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HistoryRoute(viewModel: HistoryViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    HistoryScreen(
        uiState = uiState,
        onPreviousMonth = viewModel::previousMonth,
        onNextMonth = viewModel::nextMonth,
        onDayClick = viewModel::openDayDetails,
        onCloseDayDetails = viewModel::closeDayDetails
    )
}

@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
    onCloseDayDetails: () -> Unit
) {
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    uiState.selectedDayDetails?.let { details ->
        DayDetailsScreen(details = details, onBack = onCloseDayDetails)
        return
    }

    val month = uiState.month ?: return

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onPreviousMonth) { Text("<") }
            Text(
                text = month.month.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge
            )
            Button(onClick = onNextMonth) { Text(">") }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { label ->
                Text(text = label, modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
            }
        }

        month.cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                week.forEach { cell ->
                    CalendarDayCell(cell = cell, onDayClick = onDayClick)
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    cell: CalendarDayCellUiModel,
    onDayClick: (LocalDate) -> Unit
) {
    val percentText = cell.percent?.let { "$it%" } ?: "--"
    val color = when (cell.percent ?: -1) {
        in 0..30 -> Color(0xFFD32F2F)
        in 31..70 -> Color(0xFFF9A825)
        in 71..100 -> Color(0xFF2E7D32)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = Modifier
            .width(44.dp)
            .height(56.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .clickable(enabled = cell.date != null) { cell.date?.let(onDayClick) }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = cell.date?.dayOfMonth?.toString().orEmpty(), style = MaterialTheme.typography.bodySmall)
        Text(text = percentText, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun DayDetailsScreen(
    details: DayDetailsUiModel,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onBack) { Text("Назад к календарю") }
        Text(
            text = details.date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Итог: ${details.completionPercent?.let { "$it%" } ?: "--"}",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(details.routines) { routine ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "${routine.routineTitle}: ${routine.completedItems}/${routine.totalItems}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        routine.checkedItems.forEach { item ->
                            Text(text = "• $item", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
