package com.example.seniorassist.ui.reminders

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.seniorassist.data.Reminder // Import the correct data model
import com.example.seniorassist.ui.theme.SeniorAssistTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RemindersScreen(
    reminders: List<Reminder>,
    onCreateReminder: (String, String) -> Unit,
    onUpdateReminder: (String, String) -> Unit,
    onDeleteReminder: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val groupedReminders = reminders
        .sortedBy { it.dueAt }
        .groupBy { it.dueAt.atZone(ZoneId.systemDefault()).toLocalDate() }
        .toSortedMap()

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
            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes recordatorios.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    groupedReminders.forEach { (date, remindersForDay) ->
                        stickyHeader {
                            DateHeader(date = date)
                        }
                        items(remindersForDay, key = { it.id }) { reminder ->
                            ReminderCard(reminder, onUpdateReminder, onDeleteReminder)
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddReminderDialog(
                onDismiss = { showAddDialog = false },
                onSave = {
                    onCreateReminder(it.first, it.second)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun DateHeader(date: LocalDate) {
    val headerText = when {
        date.isEqual(LocalDate.now()) -> "Hoy"
        date.isEqual(LocalDate.now().plusDays(1)) -> "Mañana"
        else -> date.format(DateTimeFormatter.ofPattern("EEE, dd MMM", Locale("es")))
    }
    Surface(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = headerText,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun ReminderCard(
    reminder: Reminder,
    onUpdateReminder: (String, String) -> Unit,
    onDeleteReminder: (String) -> Unit
) {
    val isDone = reminder.status == "done"

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDone) 0.dp else 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isDone) Icons.Filled.CheckCircle else Icons.Filled.Pending,
                contentDescription = "Estado",
                tint = if (isDone) Color.Gray else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = reminder.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                val localDateTime = LocalDateTime.ofInstant(reminder.dueAt, ZoneId.systemDefault())
                val displayTime = localDateTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
                Text(text = displayTime, style = MaterialTheme.typography.bodyMedium)
            }
            if (!isDone) {
                IconButton(onClick = { onUpdateReminder(reminder.id, "done") }) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Marcar como completado")
                }
            }
            IconButton(onClick = { onDeleteReminder(reminder.id) }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar recordatorio", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderDialog(onDismiss: () -> Unit, onSave: (Pair<String, String>) -> Unit) {
    var title by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    var hourExpanded by remember { mutableStateOf(false) }
    var minuteExpanded by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(0) } 

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)

    val selectedDateText = datePickerState.selectedDateMillis?.let {
        val instant = Instant.ofEpochMilli(it)
        val localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()
        localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()))
    } ?: "Elegir fecha"

    val hours = (0..23).toList()
    val minutes = (0..55).step(5).toList()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo Recordatorio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Button(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(selectedDateText)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    ExposedDropdownMenuBox(expanded = hourExpanded, onExpandedChange = { hourExpanded = !hourExpanded }, modifier = Modifier.weight(1f)) {
                        TextField(
                            value = String.format("%02d", selectedHour),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hora") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hourExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = hourExpanded, onDismissRequest = { hourExpanded = false }) {
                            hours.forEach { hour ->
                                DropdownMenuItem(
                                    text = { Text(String.format("%02d", hour)) },
                                    onClick = {
                                        selectedHour = hour
                                        hourExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    ExposedDropdownMenuBox(expanded = minuteExpanded, onExpandedChange = { minuteExpanded = !minuteExpanded }, modifier = Modifier.weight(1f)) {
                        TextField(
                            value = String.format("%02d", selectedMinute),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Minutos") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = minuteExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = minuteExpanded, onDismissRequest = { minuteExpanded = false }) {
                            minutes.forEach { minute ->
                                DropdownMenuItem(
                                    text = { Text(String.format("%02d", minute)) },
                                    onClick = {
                                        selectedMinute = minute
                                        minuteExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                val selectedLocalDate = Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                val selectedLocalDateTime = selectedLocalDate.atTime(selectedHour, selectedMinute)
                val formattedString = selectedLocalDateTime.atZone(ZoneId.systemDefault()).toInstant().toString()
                onSave(Pair(title, formattedString))
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RemindersScreenPreview() {
    SeniorAssistTheme {
        val previewReminders = listOf(
            Reminder("1", "Tomar pastilla de la presión", Instant.now(), "draft"),
            Reminder("2", "Paseo matutino", Instant.now().plusSeconds(86400), "draft"),
        )
        RemindersScreen(
            reminders = previewReminders,
            onCreateReminder = { _, _ -> },
            onUpdateReminder = { _, _ -> },
            onDeleteReminder = { _ -> }
        )
    }
}