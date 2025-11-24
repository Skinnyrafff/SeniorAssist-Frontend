package com.example.seniorassist.data

import com.google.gson.annotations.SerializedName

// This file contains all the data models for network requests and responses.

// --- Models for our Python Backend ---

data class ChatRequest(
    val text: String,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("session_id") val sessionId: String? = null
)

data class ChatResponse(
    val reply: String,
    val flow: String? = null,
    @SerializedName("next_prompt") val nextPrompt: String? = null,
    val emergency: Boolean = false
)

data class ReminderResponse(
    val id: String,
    val title: String,
    @SerializedName("due_at") val dueAt: String,
    val status: String,
    @SerializedName("created_at") val createdAt: String
)

data class CreateReminderRequest(
    @SerializedName("device_id") val deviceId: String,
    val title: String,
    @SerializedName("due_at") val dueAt: String?
)

// Request body for updating a reminder's status
data class UpdateReminderRequest(
    val status: String
)


// --- Models for Google Cloud TTS ---

data class SynthesisRequestBody(
    val input: SynthesisInput,
    val voice: VoiceSelectionParams,
    val audioConfig: AudioConfig
)

data class SynthesisInput(
    val text: String
)

data class VoiceSelectionParams(
    val languageCode: String,
    val name: String
)

data class AudioConfig(
    val audioEncoding: String
)

data class SynthesisResponseBody(
    val audioContent: String
)
