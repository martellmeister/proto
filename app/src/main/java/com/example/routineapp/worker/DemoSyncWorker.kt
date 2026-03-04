package com.example.routineapp.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.routineapp.data.repository.RoutineRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DemoSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: RoutineRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        repository.ensureDemoData()
        return Result.success()
    }
}
