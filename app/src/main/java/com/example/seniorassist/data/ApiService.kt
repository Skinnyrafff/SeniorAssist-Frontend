package com.example.seniorassist.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// This interface defines all our network API endpoints.
interface ApiService {

    // Google Cloud Text-to-Speech API
    @POST("https://texttospeech.googleapis.com/v1/text:synthesize")
    suspend fun synthesize(
        @Body body: SynthesisRequestBody,
        @Query("key") apiKey: String
    ): SynthesisResponseBody

    // Our Python Backend Chat API
    @POST("chat")
    suspend fun postChat(
        @Body body: ChatRequest
    ): ChatResponse

    // Our Python Backend Reminders API
    @GET("reminders")
    suspend fun getReminders(
        @Query("device_id") deviceId: String
    ): List<ReminderResponse>

    @POST("reminders")
    suspend fun createReminder(
        @Body body: CreateReminderRequest
    ): ReminderResponse

    @PATCH("reminders/{id}")
    suspend fun updateReminder(
        @Path("id") reminderId: String,
        @Body body: UpdateReminderRequest
    ): ReminderResponse

    @DELETE("reminders/{id}")
    suspend fun deleteReminder(
        @Path("id") reminderId: String
    ): Response<Unit> // Use Response<Unit> for empty responses
}
