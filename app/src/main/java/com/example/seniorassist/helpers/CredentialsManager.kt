package com.example.seniorassist.helpers

import android.content.Context
import java.util.UUID

// Singleton para gestionar las credenciales del dispositivo y del usuario
object CredentialsManager {

    private const val PREFS_NAME = "senior_assist_prefs"
    private const val KEY_DEVICE_ID = "device_id"
    private const val KEY_USER_ID = "user_id"

    // Guarda las credenciales recibidas del backend
    fun saveCredentials(context: Context, deviceId: String, userId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_DEVICE_ID, deviceId)
            .putString(KEY_USER_ID, userId)
            .apply()
    }

    // Obtiene el ID del dispositivo. Si no existe, lo crea y lo guarda.
    fun getOrCreateDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var deviceId = prefs.getString(KEY_DEVICE_ID, null)

        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply()
        }
        return deviceId
    }

    // Obtiene el ID del usuario. Devuelve null si no está guardado.
    fun getUserId(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_ID, null)
    }

    // Comprueba si el usuario está registrado (es decir, si tenemos un user_id)
    fun isUserRegistered(context: Context): Boolean {
        return getUserId(context) != null
    }
}