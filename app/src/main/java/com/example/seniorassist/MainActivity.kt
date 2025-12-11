package com.example.seniorassist

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.seniorassist.helpers.CredentialsManager
import com.example.seniorassist.ui.AssistantViewModel
import com.example.seniorassist.ui.MainScreen
import com.example.seniorassist.ui.RegistrationScreen
import com.example.seniorassist.ui.theme.SeniorAssistTheme

class MainActivity : ComponentActivity() {

    private val viewModel: AssistantViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (!isGranted) {
            // The ViewModel can handle this state if needed
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permissions on startup
        requestExactAlarmPermission()
        requestNotificationPermission()

        setContent {
            SeniorAssistTheme {
                var isUserRegistered by remember { mutableStateOf(CredentialsManager.isUserRegistered(this)) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isUserRegistered) {
                        MainScreen(
                            viewModel = viewModel,
                            requestMicPermission = ::requestMicPermission
                        )
                    } else {
                        RegistrationScreen(onRegistrationSuccess = { isUserRegistered = true })
                    }
                }
            }
        }
    }

    private fun requestMicPermission() {
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU is API 33
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // S is API 31
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    startActivity(intent)
                }
            }
        }
    }
}