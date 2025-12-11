package com.example.seniorassist.data

import com.google.gson.annotations.SerializedName
import java.time.Instant

// --- Models for our Python Backend ---

// CHAT
data class ChatRequest(
    val text: String,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("session_id") val sessionId: String
)

data class ChatResponse(
    val reply: String,
    val flow: String?,
    val emergency: Boolean,
    @SerializedName("flow_reason") val flowReason: String?,
    @SerializedName("flow_source") val flowSource: String?,
    @SerializedName("emergency_event_id") val emergencyEventId: String?,
    @SerializedName("processing_ms") val processingMs: Int?,
    val intent: IntentPrediction?,
    val sentiment: SentimentPrediction?,
    val emotion: EmotionPrediction?,
    val entities: List<NamedEntity>?
)

data class IntentPrediction(val label: String, val score: Float)
data class SentimentPrediction(val label: String, val score: Float)
data class EmotionPrediction(val label: String, val score: Float)
data class NamedEntity(val entity: String, val value: String)


// USER & DEVICE
data class UserCreateRequest(
    @SerializedName("full_name") val fullName: String
)

data class UserProfileResponse(
    val id: String,
    @SerializedName("full_name") val fullName: String
)

data class DeviceRegistrationRequest(
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("emergency_contact") val emergencyContact: String,
    @SerializedName("emergency_phone") val emergencyPhone: String
)

data class UpdateDeviceProfileRequest(
    @SerializedName("medical_notes") val medicalNotes: String?,
    val conditions: String?,
    val medications: String?
)

data class DeviceProfile(
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("user_id") val userId: String?,
    @SerializedName("owner_name") val ownerName: String?,
    @SerializedName("emergency_contact") val emergencyContact: String?,
    @SerializedName("emergency_phone") val emergencyPhone: String?,
    @SerializedName("medical_notes") val medicalNotes: String?,
    val conditions: String?,
    val medications: String?
)


// EMERGENCY
data class EmergencyStatusResponse(
    val status: String,
    val protocol: String?,
    @SerializedName("contact_name") val contactName: String?,
    @SerializedName("contact_phone") val contactPhone: String?,
    @SerializedName("emergency_id") val emergencyId: String?,
    val reason: String?
)

data class TriggerEmergencyRequest(
    @SerializedName("device_id") val deviceId: String,
    val protocol: String,
    val reason: String
)


// REMINDERS
data class Reminder(
    val id: String,
    val title: String,
    val dueAt: Instant,
    val status: String
)

data class ReminderResponse(
    val id: String,
    val title: String,
    @SerializedName("due_at") val dueAt: String,
    val status: String
)

data class CreateReminderRequest(
    @SerializedName("device_id") val deviceId: String,
    val title: String,
    @SerializedName("due_at") val dueAt: String?,
    val timezone: String?,
    val status: String? = "draft"
)

data class UpdateReminderRequest(
    val status: String
)


// --- Models for Google Cloud TTS (Unchanged) ---

data class SynthesisRequestBody(
    val input: SynthesisInput,
    val voice: VoiceSelectionParams,
    val audioConfig: AudioConfig
)

data class SynthesisInput(val text: String)
data class VoiceSelectionParams(val languageCode: String, val name: String)
data class AudioConfig(val audioEncoding: String)
data class SynthesisResponseBody(val audioContent: String)
