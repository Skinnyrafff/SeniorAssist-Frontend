package com.example.seniorassist

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.seniorassist.ui.MainScreen
import com.example.seniorassist.ui.AssistantViewModel
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

        setContent {
            SeniorAssistTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        viewModel = viewModel,
                        requestMicPermission = ::requestMicPermission // Pass the function reference
                    )
                }
            }
        }
    }

    private fun requestMicPermission() {
        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
}