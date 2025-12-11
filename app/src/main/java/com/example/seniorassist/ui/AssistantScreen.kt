package com.example.seniorassist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.seniorassist.R
import com.example.seniorassist.ui.theme.SeniorAssistTheme

@Composable
fun AssistantScreen(
    uiState: VoiceUiState,
    onScreenClick: () -> Unit,
    requestPermission: () -> Unit
) {
    LaunchedEffect(Unit) {
        requestPermission()
    }

    val screenModifier = if (uiState.screenState == ScreenState.RESPONDING) {
        Modifier.clickable { onScreenClick() }
    } else {
        Modifier
    }

    Box(
        modifier = Modifier.fillMaxSize().then(screenModifier),
        contentAlignment = Alignment.Center
    ) {
        AssistantContentUI(
            uiState = uiState
        )
    }
}

@Composable
fun AssistantContentUI(uiState: VoiceUiState) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val dotSize = 15.dp
            Box(modifier = Modifier.size(dotSize).background(Color(0xFF81D4FA), CircleShape))
            Box(modifier = Modifier.size(dotSize).background(Color(0xFF4FC3F7), CircleShape))
            Box(modifier = Modifier.size(dotSize).background(Color(0xFF29B6F6), CircleShape))
            Box(modifier = Modifier.size(dotSize).background(Color(0xFF039BE5), CircleShape))
        }
        Spacer(modifier = Modifier.height(24.dp))

        AutoResizeText(
            text = uiState.displayText,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.height(200.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        val animationSpec = when (uiState.screenState) {
            ScreenState.THINKING -> LottieCompositionSpec.RawRes(R.raw.robot_bot_2)
            else -> LottieCompositionSpec.RawRes(R.raw.robot_bot_1)
        }

        val composition by rememberLottieComposition(animationSpec)
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever,
        )
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(250.dp)
        )

        if (uiState.screenState == ScreenState.RESPONDING) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Presiona aquí para hablar",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AutoResizeText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    var scaledTextStyle by remember(text) { mutableStateOf(style) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier.alpha(if (readyToDraw) 1f else 0f), 
        style = scaledTextStyle,
        textAlign = TextAlign.Center,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowHeight) {
                scaledTextStyle = scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * 0.95f) // Reduced the shrinking factor slightly
            } else {
                readyToDraw = true
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AssistantScreenRespondingPreview() {
    SeniorAssistTheme {
        AssistantScreen(uiState = VoiceUiState(screenState = ScreenState.RESPONDING, displayText = "¡Hola! ¿En qué puedo ayudarte?"), onScreenClick = {}, requestPermission = {})
    }
}

@Preview(showBackground = true)
@Composable
fun AssistantScreenListeningPreview() {
    SeniorAssistTheme {
        AssistantScreen(uiState = VoiceUiState(screenState = ScreenState.LISTENING, displayText = "Escuchando..."), onScreenClick = {}, requestPermission = {})
    }
}

@Preview(showBackground = true)
@Composable
fun AssistantScreenThinkingPreview() {
    SeniorAssistTheme {
        AssistantScreen(uiState = VoiceUiState(screenState = ScreenState.THINKING, displayText = "Pensando..."), onScreenClick = {}, requestPermission = {})
    }
}
