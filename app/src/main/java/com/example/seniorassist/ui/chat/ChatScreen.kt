package com.example.seniorassist.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seniorassist.ui.theme.SeniorAssistTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

// --- Data Models for Chat ---
data class ChatMessage(
    val text: String,
    val author: Author,
    val timestamp: Long = System.currentTimeMillis()
)

enum class Author {
    USER, ASSISTANT
}

// --- UI ---

@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onSpeakMessage: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        coroutineScope.launch {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message, onSpeakMessage)
            }
        }

        Surface(shadowElevation = 8.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe un mensaje...") },
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onSendMessage) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar mensaje")
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage, onSpeakMessage: (String) -> Unit) {
    val isUserMessage = message.author == Author.USER
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val time = timeFormat.format(message.timestamp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUserMessage) {
            IconButton(onClick = { onSpeakMessage(message.text) }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.PlayCircleOutline, contentDescription = "Reproducir mensaje", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.width(4.dp))
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUserMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(16.dp, 16.dp, if(isUserMessage) 4.dp else 16.dp, if(isUserMessage) 16.dp else 4.dp),
            modifier = Modifier.widthIn(max = 280.dp) 
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(text = message.text)
                Text(
                    text = time,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    SeniorAssistTheme {
        val previewMessages = remember {
            mutableStateListOf(
                ChatMessage("Hola, ¿en qué puedo ayudarte?", Author.ASSISTANT)
            )
        }
        ChatScreen(
            messages = previewMessages,
            inputText = "",
            onInputChange = {},
            onSendMessage = {},
            onSpeakMessage = {}
        )
    }
}
