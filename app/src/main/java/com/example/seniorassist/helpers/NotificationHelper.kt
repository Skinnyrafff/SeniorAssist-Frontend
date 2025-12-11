package com.example.seniorassist.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.seniorassist.R

object NotificationHelper {

    private const val CHANNEL_ID = "senior_assist_channel"

    fun showSimpleNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación para Android 8.0+
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Alertas de Senior Assist",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones y recordatorios importantes."
        }
        notificationManager.createNotificationChannel(channel)

        // Usar el sonido de alarma predeterminado del sistema
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(alarmSound)
            .setAutoCancel(true)
            .build()

        // Hacer que el sonido se repita hasta que el usuario interactúe con la notificación
        notification.flags = notification.flags or Notification.FLAG_INSISTENT

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}