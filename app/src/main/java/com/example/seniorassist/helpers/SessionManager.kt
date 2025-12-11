package com.example.seniorassist.helpers

import java.util.UUID

// Singleton para gestionar el ID de la sesión de conversación
object SessionManager {

    private var currentSessionId: String? = null

    // Inicia una nueva sesión, generando un nuevo ID.
    fun startNewSession() {
        currentSessionId = UUID.randomUUID().toString()
    }

    // Obtiene el ID de la sesión actual. Si no hay una, la inicia.
    fun getSessionId(): String {
        if (currentSessionId == null) {
            startNewSession()
        }
        return currentSessionId!!
    }
}