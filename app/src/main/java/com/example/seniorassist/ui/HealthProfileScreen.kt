package com.example.seniorassist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HealthProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: HealthProfileViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil de Salud") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Input Section
            AddItemSection(viewModel)

            Spacer(modifier = Modifier.height(24.dp))

            // Display Section
            if (isLoading && viewModel.medicalNotes.isEmpty() && viewModel.conditions.isEmpty() && viewModel.medications.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                HealthChipGroup(viewModel, "Notas Médicas", viewModel.medicalNotes, HealthCategory.NOTE, Color(0xFFBBDEFB))
                HealthChipGroup(viewModel, "Condiciones de Salud", viewModel.conditions, HealthCategory.CONDITION, Color(0xFFFFE0B2))
                HealthChipGroup(viewModel, "Medicamentos Actuales", viewModel.medications, HealthCategory.MEDICATION, Color(0xFFFFCDD2))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddItemSection(viewModel: HealthProfileViewModel) {
    Column {
        OutlinedTextField(
            value = viewModel.newItemText.value,
            onValueChange = { viewModel.newItemText.value = it },
            label = { Text("Añadir nuevo dato...") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { viewModel.addItem() }) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir")
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HealthCategory.values().forEach { category ->
                FilterChip(
                    selected = viewModel.selectedCategory.value == category,
                    onClick = { viewModel.selectedCategory.value = category },
                    label = { Text(category.displayName) } // Use displayName for Spanish text
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun HealthChipGroup(
    viewModel: HealthProfileViewModel,
    title: String,
    items: List<String>,
    category: HealthCategory,
    chipColor: Color
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items.forEach { item ->
                InputChip(
                    selected = false,
                    onClick = { /* No action on click */ },
                    label = { Text(item) },
                    colors = InputChipDefaults.inputChipColors(containerColor = chipColor),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.removeItem(item, category) }, modifier = Modifier.size(18.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Eliminar")
                        }
                    }
                )
            }
        }
    }
}