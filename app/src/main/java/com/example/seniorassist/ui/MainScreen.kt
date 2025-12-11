package com.example.seniorassist.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.seniorassist.data.EmergencyStatusResponse
import com.example.seniorassist.ui.chat.ChatScreen
import com.example.seniorassist.ui.reminders.RemindersScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Voice : Screen("voice", "Voz", Icons.Default.RecordVoiceOver)
    object Chat : Screen("chat", "Chat", Icons.Default.Chat)
    object Reminders : Screen("reminders", "Recordatorios", Icons.Default.Notifications)
    object HealthProfile : Screen("healthProfile", "Perfil", Icons.Default.MedicalInformation)
}

private val items = listOf(Screen.Voice, Screen.Chat, Screen.Reminders, Screen.HealthProfile)

@Composable
fun MainScreen(viewModel: AssistantViewModel, requestMicPermission: () -> Unit) {
    val navController = rememberNavController()
    val emergencyStatus by viewModel.emergencyStatus

    val callPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.triggerManualEmergency()
            }
        }
    )

    Scaffold(
        topBar = {
            AnimatedVisibility(visible = emergencyStatus != null && emergencyStatus?.status == "emergency") {
                EmergencyBanner(emergencyStatus, onCancel = { viewModel.stopEmergencyPolling() })
            }
        },
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            if (navBackStackEntry?.destination?.route == Screen.Voice.route) {
                FloatingActionButton(
                    onClick = { callPermissionLauncher.launch(Manifest.permission.CALL_PHONE) },
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Sos, contentDescription = "Boton de Emergencia")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = Screen.Voice.route, 
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Voice.route) {
                AssistantScreen(uiState = viewModel.voiceUiState.value, onScreenClick = { viewModel.startListening() }, requestPermission = requestMicPermission)
            }
            composable(Screen.Chat.route) {
                ChatScreen(
                    messages = viewModel.chatMessages,
                    inputText = viewModel.chatInputText.value,
                    onInputChange = { viewModel.onChatInputChanged(it) },
                    onSendMessage = { viewModel.sendChatMessage() },
                    onSpeakMessage = { viewModel.speakChatMessage(it) }
                )
            }
            composable(Screen.Reminders.route) {
                RemindersScreen(
                    reminders = viewModel.reminders,
                    onCreateReminder = { title, dueAt -> viewModel.createReminder(title, dueAt) },
                    onUpdateReminder = { id, status -> viewModel.updateReminderStatus(id, status) },
                    onDeleteReminder = { id -> viewModel.deleteReminder(id) }
                )
            }
            composable(Screen.HealthProfile.route) {
                HealthProfileScreen(onNavigateBack = { navController.navigateUp() })
            }
        }
    }
}

@Composable
fun EmergencyBanner(emergencyStatus: EmergencyStatusResponse?, onCancel: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text("¡EMERGENCIA ACTIVADA!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
            Text(emergencyStatus?.reason ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("CANCELAR")
        }
    }
}