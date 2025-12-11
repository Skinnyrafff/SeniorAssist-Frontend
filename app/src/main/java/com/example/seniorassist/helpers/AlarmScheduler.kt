package com.example.seniorassist.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.seniorassist.receivers.MedicineReminderReceiver
import java.util.Calendar

object AlarmScheduler {

    fun scheduleReminder(context: Context, time: Calendar, reminderText: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, MedicineReminderReceiver::class.java).apply {
            putExtra("REMINDER_TEXT", reminderText)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderText.hashCode(), // Use the same ID for scheduling and canceling
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule the alarm if permission is granted
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, reminderText: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MedicineReminderReceiver::class.java).apply {
            putExtra("REMINDER_TEXT", reminderText)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderText.hashCode(), // Use the same ID to find the existing alarm
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}