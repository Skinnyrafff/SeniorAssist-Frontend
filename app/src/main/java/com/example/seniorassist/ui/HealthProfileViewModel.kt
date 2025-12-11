package com.example.seniorassist.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.seniorassist.data.RetrofitClient
import com.example.seniorassist.data.UpdateDeviceProfileRequest
import com.example.seniorassist.helpers.CredentialsManager
import kotlinx.coroutines.launch

enum class HealthCategory(val displayName: String) {
    NOTE("Nota"),
    CONDITION("Condición"),
    MEDICATION("Medicación")
}

class HealthProfileViewModel(application: Application) : AndroidViewModel(application) {

    // UI State
    val medicalNotes = mutableStateListOf<String>()
    val conditions = mutableStateListOf<String>()
    val medications = mutableStateListOf<String>()

    val newItemText = mutableStateOf("")
    val selectedCategory = mutableStateOf(HealthCategory.NOTE)

    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val successMessage = mutableStateOf<String?>(null)

    init {
        loadHealthProfile()
    }

    fun loadHealthProfile() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null
            try {
                val context = getApplication<Application>().applicationContext
                val deviceId = CredentialsManager.getOrCreateDeviceId(context)
                val profile = RetrofitClient.backendService.getDeviceProfile(deviceId)

                // Clear and populate lists
                medicalNotes.clear()
                profile.medicalNotes?.let { medicalNotes.addAll(it.split(";").filter { it.isNotBlank() }) }
                conditions.clear()
                profile.conditions?.let { conditions.addAll(it.split(";").filter { it.isNotBlank() }) }
                medications.clear()
                profile.medications?.let { medications.addAll(it.split(";").filter { it.isNotBlank() }) }

            } catch (e: Exception) {
                errorMessage.value = "No se pudo cargar el perfil de salud."
            } finally {
                isLoading.value = false
            }
        }
    }

    fun addItem() {
        val text = newItemText.value.trim()
        if (text.isBlank()) return

        when (selectedCategory.value) {
            HealthCategory.NOTE -> medicalNotes.add(text)
            HealthCategory.CONDITION -> conditions.add(text)
            HealthCategory.MEDICATION -> medications.add(text)
        }
        newItemText.value = ""
        saveHealthProfile() // Auto-save on change
    }

    fun removeItem(item: String, category: HealthCategory) {
        when (category) {
            HealthCategory.NOTE -> medicalNotes.remove(item)
            HealthCategory.CONDITION -> conditions.remove(item)
            HealthCategory.MEDICATION -> medications.remove(item)
        }
        saveHealthProfile() // Auto-save on change
    }

    private fun saveHealthProfile() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            successMessage.value = null
            try {
                val context = getApplication<Application>().applicationContext
                val deviceId = CredentialsManager.getOrCreateDeviceId(context)

                val request = UpdateDeviceProfileRequest(
                    medicalNotes = medicalNotes.joinToString(";"),
                    conditions = conditions.joinToString(";"),
                    medications = medications.joinToString(";")
                )

                RetrofitClient.backendService.updateDeviceProfile(deviceId, request)
                successMessage.value = "¡Perfil guardado!"

            } catch (e: Exception) {
                errorMessage.value = "No se pudo guardar el perfil."
            } finally {
                isLoading.value = false
            }
        }
    }
}