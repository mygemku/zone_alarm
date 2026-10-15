package com.zonealarm.app.alarm

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.zonealarm.app.domain.repo.ZoneRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SnoozeEntryPoint {
    fun repository(): ZoneRepository
    fun notifier(): AlarmNotifier
}

/**
 * Re-fires a zone alarm after a snooze delay. Plain CoroutineWorker reaching
 * singletons through a Hilt entry point (no hilt-work setup needed).
 */
class SnoozeWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    companion object {
        const val KEY_ZONE_ID = "zone_id"
        const val SNOOZE_MINUTES = 10L

        fun schedule(context: Context, zoneId: String) {
            val request = OneTimeWorkRequestBuilder<SnoozeWorker>()
                .setInitialDelay(SNOOZE_MINUTES, TimeUnit.MINUTES)
                .setInputData(workDataOf(KEY_ZONE_ID to zoneId))
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
    }

    override suspend fun doWork(): Result {
        val zoneId = inputData.getString(KEY_ZONE_ID) ?: return Result.failure()
        val entry = EntryPointAccessors.fromApplication(
            applicationContext,
            SnoozeEntryPoint::class.java,
        )
        val zone = entry.repository().getZone(zoneId) ?: return Result.failure()
        entry.notifier().fire(zone)
        return Result.success()
    }
}
