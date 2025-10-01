package com.example.habbittracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.habbittracker.data.PreferencesHelper
import com.example.habbittracker.work.HydrationWorker
import java.util.concurrent.TimeUnit

/**
 * BroadcastReceiver to reschedule hydration reminders after device reboot
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val preferencesHelper = PreferencesHelper(context)
            
            // Only reschedule if hydration reminders are enabled
            if (preferencesHelper.isHydrationEnabled()) {
                scheduleHydrationWork(context, preferencesHelper)
            }
        }
    }
    
    private fun scheduleHydrationWork(context: Context, preferencesHelper: PreferencesHelper) {
        val intervalMinutes = preferencesHelper.getHydrationIntervalMinutes()
        
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        
        val hydrationWork = PeriodicWorkRequestBuilder<HydrationWorker>(
            intervalMinutes.toLong(),
            TimeUnit.MINUTES,
            15, // Flex interval
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            HydrationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            hydrationWork
        )
    }
}
