package com.example.seniorassist.ui

// Defines the possible visual states of the assistant screen.
enum class ScreenState {
    LISTENING,  // Actively listening to the user
    THINKING,   // Waiting for a response from the backend
    RESPONDING  // Displaying a response, an error, or a welcome message
}

// Holds the complete state for the VOICE UI to display.
data class VoiceUiState(
    val screenState: ScreenState = ScreenState.RESPONDING,
    val displayText: String = "Hola, ¿en qué puedo ayudarte?"
)
