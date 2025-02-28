package com.yoiberdev.uberclone.presentation.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yoiberdev.uberclone.domain.model.Message

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState

    var newMessage by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        // Lista de mensajes
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(uiState.messages) { message ->
                MessageItem(message)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Campo para enviar mensaje
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedTextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                modifier = Modifier.weight(1f),
                label = { Text("Escribe un mensaje") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                viewModel.sendMessage(newMessage, senderId = "currentUserId")
                newMessage = ""
            }) {
                Text("Enviar")
            }
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "De: ${message.senderId}")
        Text(text = message.text)
    }
}