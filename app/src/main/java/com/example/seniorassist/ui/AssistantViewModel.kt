package com.example.seniorassist.ui

import android.app.Application
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.util.Base64
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.seniorassist.BuildConfig
import com.example.seniorassist.data.*
import com.example.seniorassist.ui.chat.Author
import com.example.seniorassist.ui.chat.ChatMessage
import com.example.seniorassist.ui.reminders.Reminder
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class AssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)

    // --- UI States ---
    var voiceUiState = mutableStateOf(VoiceUiState())
        private set
    val chatMessages = mutableStateListOf<ChatMessage>()
    var chatInputText = mutableStateOf("")
        private set
    val reminders = mutableStateListOf<Reminder>()
        private set

    private var mediaPlayer: MediaPlayer? = null
    private val deviceId: String by lazy { "dev1" }

    init {
        setupSpeechRecognizer()
        val welcomeMessage = voiceUiState.value.displayText
        chatMessages.add(ChatMessage(welcomeMessage, Author.ASSISTANT))
        loadReminders()
    }

    // --- Reminders Functions ---
    fun loadReminders() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.backendService.getReminders(deviceId)
                val reminderList = response.map { reminderResponse ->
                    val odt = OffsetDateTime.parse(reminderResponse.dueAt)
                    Reminder(
                        id = reminderResponse.id,
                        title = reminderResponse.title,
                        dueAt = odt.toInstant().toEpochMilli(),
                        status = reminderResponse.status
                    )
                }
                reminders.clear()
                reminders.addAll(reminderList)
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to load reminders", e)
            }
        }
    }

    fun createReminder(title: String, dueAtString: String) {
        viewModelScope.launch {
            try {
                val isoString = convertToIso8601(dueAtString)
                val request = CreateReminderRequest(
                    deviceId = deviceId,
                    title = title,
                    dueAt = isoString
                )
                RetrofitClient.backendService.createReminder(request)
                loadReminders() // Refresh the list
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to create reminder", e)
            }
        }
    }

    fun updateReminderStatus(reminderId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                val request = UpdateReminderRequest(status = newStatus)
                RetrofitClient.backendService.updateReminder(reminderId, request)
                loadReminders() // Refresh the list
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to update reminder", e)
            }
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.backendService.deleteReminder(reminderId)
                loadReminders() // Refresh the list
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to delete reminder", e)
            }
        }
    }

    private fun convertToIso8601(dateTimeString: String): String? {
        return try {
            val localFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val localDateTime = LocalDateTime.parse(dateTimeString, localFormatter)
            val zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
            zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        } catch (e: Exception) {
            null
        }
    }

    // --- Speech and Chat Processing ---
    // ... (Code for speech and chat remains the same)

    override fun onCleared() {
        super.onCleared()
        speechRecognizer.destroy()
        mediaPlayer?.release()
    }
}