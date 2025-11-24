package com.example.seniorassist.ui.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.seniorassist.ui.theme.SeniorAssistTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- Data Models for Reminders (simplified for UI) ---
data class Reminder(
    val id: String,
    val title: String,
    val dueAt: Long,
    val status: String
)

// --- UI ---

@Composable
fun RemindersScreen(
    reminders: List<Reminder>,
    onCreateReminder: (String, String) -> Unit,
    onUpdateReminder: (String, String) -> Unit,
    onDeleteReminder: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir recordatorio")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Text(
                text = "Mis Recordatorios",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reminders) { reminder ->
                    ReminderCard(reminder, onUpdateReminder, onDeleteReminder)
                }
            }
        }

        if (showAddDialog) {
            AddReminderDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, dueAt ->
                    onCreateReminder(title, dueAt)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    onUpdateReminder: (String, String) -> Unit,
    onDeleteReminder: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault()) }
    val statusColor = when (reminder.status) {
        "confirmed" -> MaterialTheme.colorScheme.primary
        "draft" -> MaterialTheme.colorScheme.tertiary
        "done" -> Color.Gray
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (reminder.status == "done") Icons.Default.CheckCircle else Icons.Default.Pending,
                contentDescription = "Estado",
                tint = statusColor
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = reminder.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = dateFormat.format(Date(reminder.dueAt)), style = MaterialTheme.typography.bodyMedium)
                Text(text = "Estado: ${reminder.status}", style = MaterialTheme.typography.bodySmall, color = statusColor)
            }
            if (reminder.status != "done") {
                IconButton(onClick = { onUpdateReminder(reminder.id, "done") }) {
                    Icon(Icons.Default.Check, contentDescription = "Marcar como completado")
                }
            }
            IconButton(onClick = { onDeleteReminder(reminder.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar recordatorio", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddReminderDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var dueAt by remember { mutableState of("") }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Nuevo Recordatorio", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dueAt,
                    onValueChange = { dueAt = it },
                    label = { Text("Fecha y Hora (YYYY-MM-DD HH:MM)") },
                    placeholder = { Text("Ej: 2024-12-25 14:30") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onSave(title, dueAt) }) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RemindersScreenPreview() {
    SeniorAssistTheme {
        val previewReminders = listOf(
            Reminder("1", "Tomar pastilla de la presión", System.currentTimeMillis() + 3600000, "confirmed"),
        )
        RemindersScreen(
            reminders = previewReminders, 
            onCreateReminder = {_,_ -> }, 
            onUpdateReminder = {_,_ -> },
            onDeleteReminder = {}
        )
    }
}
