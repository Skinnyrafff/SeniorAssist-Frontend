package com.example.seniorassist.helpers

import android.content.Context
import java.util.UUID

// Usamos un 'object' para que sea un Singleton (una única instancia en toda la app)
object DeviceIdManager {

    private const val PREFS_NAME = "senior_assist_prefs"
    private const val KEY_DEVICE_ID = "device_id"

    // Obtiene el ID guardado. Si no existe, lo crea, lo guarda y lo devuelve.
    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)

        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        return deviceId
    }
}