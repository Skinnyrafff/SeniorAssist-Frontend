package com.example.seniorassist.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.seniorassist.helpers.NotificationHelper

// Este BroadcastReceiver se "despierta" cuando la alarma suena
class MedicineReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderText = intent.getStringExtra("REMINDER_TEXT") ?: "Es hora de tu recordatorio."
        
        // Llama al helper para construir y mostrar la notificación
        NotificationHelper.showSimpleNotification(
            context = context,
            title = "Recordatorio",
            message = reminderText
        )
    }
}