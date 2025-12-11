package com.example.seniorassist.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// This interface defines all our network API endpoints.
interface ApiService {

    // --- Google Cloud Text-to-Speech API ---
    @POST("https://texttospeech.googleapis.com/v1/text:synthesize")
    suspend fun synthesize(
        @Body body: SynthesisRequestBody,
        @Query("key") apiKey: String
    ): SynthesisResponseBody

    // --- Senior Assist Backend ---

    // User
    @POST("users")
    suspend fun createUser(@Body request: UserCreateRequest): UserProfileResponse

    // Device
    @POST("devices/register")
    suspend fun registerDevice(@Body request: DeviceRegistrationRequest)

    @GET("devices/{deviceId}")
    suspend fun getDeviceProfile(@Path("deviceId") deviceId: String): DeviceProfile

    @PUT("devices/{deviceId}")
    suspend fun updateDeviceProfile(
        @Path("deviceId") deviceId: String,
        @Body request: UpdateDeviceProfileRequest
    )

    // Chat
    @POST("chat")
    suspend fun postChat(
        @Body body: ChatRequest
    ): ChatResponse

    // Reminders
    @GET("reminders")
    suspend fun getReminders(
        @Query("device_id") deviceId: String
    ): List<ReminderResponse>

    @POST("reminders")
    suspend fun createReminder(
        @Body body: CreateReminderRequest
    ): ReminderResponse

    @PATCH("reminders/{reminder_id}")
    suspend fun updateReminder(
        @Path("reminder_id") reminderId: String,
        @Body body: UpdateReminderRequest
    ): ReminderResponse

    @DELETE("reminders/{reminder_id}")
    suspend fun deleteReminder(
        @Path("reminder_id") reminderId: String
    ): Response<Unit>

    // Emergency
    @POST("trigger-emergency")
    suspend fun triggerEmergency(@Body request: TriggerEmergencyRequest)

    @GET("emergency-status/{deviceId}")
    suspend fun getEmergencyStatus(@Path("deviceId") deviceId: String): EmergencyStatusResponse
}
