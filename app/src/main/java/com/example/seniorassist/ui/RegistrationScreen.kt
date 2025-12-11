package com.example.seniorassist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seniorassist.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    onRegistrationSuccess: () -> Unit,
    registrationViewModel: RegistrationViewModel = viewModel()
) {
    val isLoading = registrationViewModel.isLoading.value
    val errorMessage = registrationViewModel.errorMessage.value

    LaunchedEffect(Unit) {
        registrationViewModel.registrationSuccess.collect {
            onRegistrationSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Crear Cuenta") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Icon(
                painter = painterResource(id = R.drawable.baseline_spoke_24),
                contentDescription = "Icono de Asistente de Voz",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("¡Bienvenido!", style = MaterialTheme.typography.headlineLarge)
            Text(
                text = "Para empezar, necesitamos algunos datos para configurar tu cuenta.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = registrationViewModel.ownerName.value,
                onValueChange = { registrationViewModel.ownerName.value = it },
                label = { Text("Tu nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = registrationViewModel.emergencyContact.value,
                onValueChange = { registrationViewModel.emergencyContact.value = it },
                label = { Text("Nombre del contacto de emergencia") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Group, contentDescription = null) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = registrationViewModel.emergencyPhone.value,
                onValueChange = { registrationViewModel.emergencyPhone.value = it },
                label = { Text("Teléfono del contacto de emergencia") },
                placeholder = { Text("Ej: +56912345678") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            // Pushes the button to the bottom
            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { registrationViewModel.onRegisterClick() },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("REGISTRAR DISPOSITIVO", modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            errorMessage?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}