package com.example.seniorassist.ui

import android.app.Application
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.seniorassist.BuildConfig
import com.example.seniorassist.data.*
import com.example.seniorassist.helpers.AlarmScheduler
import com.example.seniorassist.helpers.CredentialsManager
import com.example.seniorassist.helpers.SessionManager
import com.example.seniorassist.ui.chat.Author
import com.example.seniorassist.ui.chat.ChatMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.*

class AssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(application)

    // --- UI States ---
    var voiceUiState = mutableStateOf(VoiceUiState())
        private set
    val chatMessages = mutableStateListOf<ChatMessage>()
    var chatInputText = mutableStateOf("")
        private set
    var reminders by mutableStateOf<List<Reminder>>(emptyList())
        private set
    var emergencyStatus = mutableStateOf<EmergencyStatusResponse?>(null)
        private set

    private var mediaPlayer: MediaPlayer? = null
    private val deviceId: String by lazy { CredentialsManager.getOrCreateDeviceId(application.applicationContext) }
    private var pollingJob: Job? = null

    init {
        SessionManager.startNewSession()
        setupSpeechRecognizer()
        val welcomeMessage = voiceUiState.value.displayText
        chatMessages.add(ChatMessage(welcomeMessage, Author.ASSISTANT))
        loadReminders()
    }

    // --- Emergency Functions ---
    fun triggerManualEmergency() {
        viewModelScope.launch {
            try {
                val request = TriggerEmergencyRequest(deviceId = deviceId, protocol = "call_family", reason = "Boton SOS presionado")
                RetrofitClient.backendService.triggerEmergency(request)
                startEmergencyPolling()
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to trigger emergency", e)
            }
        }
    }

    private fun startEmergencyPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (true) {
                try {
                    val status = RetrofitClient.backendService.getEmergencyStatus(deviceId)
                    emergencyStatus.value = status
                    if (status.status == "ok") {
                        stopEmergencyPolling()
                    } else {
                        status.contactPhone?.let { initiatePhoneCall(it) }
                    }
                } catch (e: Exception) {
                    Log.e("ViewModel", "Polling failed", e)
                }
                delay(15000)
            }
        }
    }

    fun stopEmergencyPolling() {
        pollingJob?.cancel()
        emergencyStatus.value = null
    }

    private fun initiatePhoneCall(phoneNumber: String) {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: SecurityException) {
            Log.e("ViewModel", "Permission to call not granted", e)
        }
    }

    // --- Reminders Functions ---
    fun loadReminders() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.backendService.getReminders(deviceId)
                val reminderList = response
                    .filter { it.status != "cancelled" }
                    .mapNotNull { reminderResponse ->
                    parseDateTimeString(reminderResponse.dueAt)?.let {
                        Reminder(
                            id = reminderResponse.id,
                            title = reminderResponse.title,
                            dueAt = it,
                            status = reminderResponse.status
                        )
                    }
                }
                reminders = reminderList
                scheduleReminders(reminderList)
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to load reminders", e)
            }
        }
    }

    private fun parseDateTimeString(dateTimeString: String?): Instant? {
        if (dateTimeString == null) return null
        // Try parsing standard ISO 8601 format first
        try {
            return Instant.parse(dateTimeString)
        } catch (e: DateTimeParseException) {
            // Fallback to a more common format like "yyyy-MM-dd HH:mm:ss"
            try {
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC") // Assume backend sends UTC
                }
                return formatter.parse(dateTimeString)?.toInstant()
            } catch (e2: Exception) {
                Log.e("ViewModel", "Failed to parse date string with any known format: $dateTimeString", e2)
                return null
            }
        }
    }

    private fun scheduleReminders(reminders: List<Reminder>) {
        val context = getApplication<Application>().applicationContext
        reminders.forEach { reminder ->
            if (reminder.status == "draft" && reminder.dueAt.isAfter(Instant.now())) {
                val calendar = Calendar.getInstance().apply { timeInMillis = reminder.dueAt.toEpochMilli() }
                AlarmScheduler.scheduleReminder(context, calendar, reminder.title)
            }
        }
    }

    fun createReminder(title: String, dueAtString: String) {
        viewModelScope.launch {
            try {
                val request = CreateReminderRequest(
                    deviceId = deviceId, title = title, dueAt = dueAtString,
                    timezone = TimeZone.getDefault().id, status = "draft"
                )
                RetrofitClient.backendService.createReminder(request)
                loadReminders()
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to create reminder", e)
            }
        }
    }

    fun updateReminderStatus(reminderId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                val reminder = reminders.find { it.id == reminderId }
                if (reminder != null && newStatus != "draft") {
                    AlarmScheduler.cancelReminder(getApplication(), reminder.title)
                }
                val request = UpdateReminderRequest(status = newStatus)
                RetrofitClient.backendService.updateReminder(reminderId, request)
                loadReminders()
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to update reminder", e)
            }
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            try {
                val reminder = reminders.find { it.id == reminderId }
                if (reminder != null) {
                    AlarmScheduler.cancelReminder(getApplication(), reminder.title)
                }
                RetrofitClient.backendService.deleteReminder(reminderId)
                loadReminders()
            } catch (e: Exception) {
                Log.e("ViewModel", "Failed to delete reminder", e)
            }
        }
    }

    // --- Speech and Chat Processing ---
    private fun setupSpeechRecognizer() {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { updateVoiceState(ScreenState.LISTENING, "Escuchando...") }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                if (voiceUiState.value.screenState != ScreenState.LISTENING) return
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No te he entendido. Toca para reintentar."
                    else -> "Hubo un error con el reconocimiento de voz."
                }
                updateVoiceState(ScreenState.RESPONDING, errorMessage)
                speakWithCloudTts(errorMessage)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    processCommand(cleanTranscription(matches[0]), Author.USER, shouldSpeak = true)
                } else {
                    val errorMessage = "No te he entendido. Toca para reintentar."
                    updateVoiceState(ScreenState.RESPONDING, errorMessage)
                    speakWithCloudTts(errorMessage)
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun processCommand(command: String, author: Author, shouldSpeak: Boolean) {
        if (author == Author.USER) chatMessages.add(ChatMessage(command, author))
        if (shouldSpeak) updateVoiceState(ScreenState.THINKING, "Pensando...")

        viewModelScope.launch {
            try {
                val sessionId = SessionManager.getSessionId()
                val request = ChatRequest(deviceId = deviceId, text = command, sessionId = sessionId)
                val response = RetrofitClient.backendService.postChat(request)
                chatMessages.add(ChatMessage(response.reply, Author.ASSISTANT))

                if (response.emergency) {
                    startEmergencyPolling()
                }

                if (shouldSpeak) {
                    val textToSpeak = if (response.emergency) "¡Emergencia detectada! " + response.reply else response.reply
                    updateVoiceState(ScreenState.RESPONDING, textToSpeak)
                    speakWithCloudTts(textToSpeak)
                }
            } catch (e: Exception) {
                val errorMsg = "No se pudo conectar con el asistente."
                if (shouldSpeak) {
                    updateVoiceState(ScreenState.RESPONDING, errorMsg)
                    speakWithCloudTts(errorMsg)
                }
                chatMessages.add(ChatMessage(errorMsg, Author.ASSISTANT))
            }
        }
    }

    fun onChatInputChanged(newText: String) { chatInputText.value = newText }

    fun sendChatMessage() {
        val textToSend = chatInputText.value
        if (textToSend.isNotBlank()) {
            processCommand(textToSend, Author.USER, shouldSpeak = false)
            chatInputText.value = ""
        }
    }

    fun speakChatMessage(text: String) { speakWithCloudTts(text) }

    private fun speakWithCloudTts(text: String) {
        viewModelScope.launch {
            try {
                val request = SynthesisRequestBody(SynthesisInput(text), VoiceSelectionParams("es-US", "es-US-Wavenet-A"), AudioConfig("MP3"))
                val response = RetrofitClient.ttsService.synthesize(request, BuildConfig.GOOGLE_CLOUD_TTS_API_KEY)
                playAudio(Base64.decode(response.audioContent, Base64.DEFAULT))
            } catch (e: Exception) {
                updateVoiceState(ScreenState.RESPONDING, "Error al generar la voz.")
            }
        }
    }

    private fun playAudio(audioBytes: ByteArray) {
        try {
            val tempMp3 = File.createTempFile("speech", "mp3", getApplication<Application>().cacheDir)
            tempMp3.deleteOnExit()
            FileOutputStream(tempMp3).use { it.write(audioBytes) }
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempMp3.absolutePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            updateVoiceState(ScreenState.RESPONDING, "Error al reproducir el audio.")
        }
    }

    fun startListening() {
        if (voiceUiState.value.screenState == ScreenState.RESPONDING) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            }
            speechRecognizer.startListening(intent)
        }
    }

    private fun updateVoiceState(newState: ScreenState, newText: String) { voiceUiState.value = VoiceUiState(screenState = newState, displayText = newText) }

    private fun cleanTranscription(text: String): String = text.trim().replace(Regex("\\s+"), " ")

    private fun convertToIso8601(dateTimeString: String): String? {
        return try {
            val localFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = localFormatter.parse(dateTimeString)
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            isoFormatter.format(date!!)
        } catch (e: Exception) { null }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer.destroy()
        mediaPlayer?.release()
        pollingJob?.cancel()
    }
}
