package com.example.seniorassist.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.seniorassist.data.DeviceRegistrationRequest
import com.example.seniorassist.data.RetrofitClient
import com.example.seniorassist.data.UserCreateRequest
import com.example.seniorassist.helpers.CredentialsManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    // State for text fields
    val ownerName = mutableStateOf("")
    val emergencyContact = mutableStateOf("")
    val emergencyPhone = mutableStateOf("")

    // State for the UI
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    // Flow to notify the UI of successful registration
    val registrationSuccess = MutableSharedFlow<Unit>()

    fun onRegisterClick() {
        if (ownerName.value.isBlank() || emergencyContact.value.isBlank() || emergencyPhone.value.isBlank()) {
            errorMessage.value = "Por favor, rellena todos los campos."
            return
        }

        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext

                // Step 1: Create the user
                val userRequest = UserCreateRequest(fullName = ownerName.value)
                val userResponse = RetrofitClient.backendService.createUser(userRequest)
                val userId = userResponse.id

                // Step 2: Register the device
                val deviceId = CredentialsManager.getOrCreateDeviceId(context)
                val deviceRequest = DeviceRegistrationRequest(
                    deviceId = deviceId,
                    userId = userId,
                    emergencyContact = emergencyContact.value,
                    emergencyPhone = emergencyPhone.value
                )
                RetrofitClient.backendService.registerDevice(deviceRequest)

                // Step 3: Save credentials and notify UI
                CredentialsManager.saveCredentials(context, deviceId, userId)
                registrationSuccess.emit(Unit)

            } catch (e: Exception) {
                errorMessage.value = "No se pudo completar el registro. Inténtalo de nuevo."
            } finally {
                isLoading.value = false
            }
        }
    }
}