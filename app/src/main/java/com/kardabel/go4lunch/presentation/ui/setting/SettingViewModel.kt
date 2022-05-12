package com.kardabel.go4lunch.presentation.ui.setting

import android.content.Context
import androidx.lifecycle.*
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.kardabel.go4lunch.domain.repository.NotificationsRepository
import com.kardabel.go4lunch.util.PermissionsViewAction
import com.kardabel.go4lunch.util.SingleLiveEvent
import com.kardabel.go4lunch.workmanager.UploadWorker
import java.time.Clock
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class SettingViewModel constructor(
    private val notificationsRepository: NotificationsRepository,
    context: Context,
    private val clock: Clock,
) : ViewModel() {

    companion object {
        const val REMINDER_REQUEST = "my reminder"
    }

    private val workManager = WorkManager.getInstance(context)

    val actionSingleLiveEvent = SingleLiveEvent<PermissionsViewAction>()

    val getSwitchPosition = notificationsRepository.isNotificationEnabledLiveData.map { switchPosition ->

        if (!switchPosition) {

            workManager.cancelAllWork()
            actionSingleLiveEvent.value = PermissionsViewAction.NOTIFICATION_DISABLED
            return@map 1

        } else {

            activateNotification()
            actionSingleLiveEvent.value = PermissionsViewAction.NOTIFICATION_ENABLED
            return@map 2

        }
    }

    private fun activateNotification() {

        val currentDate: LocalDateTime = LocalDateTime.now(clock)
        var thisNoon = currentDate.with(LocalTime.of(12, 0))

        if (currentDate.isAfter(thisNoon)) {
            thisNoon = thisNoon.plusDays(1)
        }

        val timeLeft = ChronoUnit.MILLIS
            .between(currentDate, thisNoon)

        val workRequest = PeriodicWorkRequest.Builder(
            UploadWorker::class.java,
            1,
            TimeUnit.DAYS)
            .setInitialDelay(timeLeft, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            REMINDER_REQUEST,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest)
    }
    fun notificationChange() {
        notificationsRepository.switchNotification()
    }
}