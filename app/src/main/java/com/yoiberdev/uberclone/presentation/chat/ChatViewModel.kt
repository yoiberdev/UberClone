package com.yoiberdev.uberclone.presentation.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoiberdev.uberclone.domain.model.Message
import com.yoiberdev.uberclone.domain.usecase.ListenChatMessagesUseCase
import com.yoiberdev.uberclone.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ChatViewModel(
    private val sendMessageUseCase: SendMessageUseCase,
    private val listenChatMessagesUseCase: ListenChatMessagesUseCase,
    private val conversationId: String // Puedes inyectar o pasar el id de conversación
) : ViewModel() {

    var uiState by mutableStateOf(ChatUiState())
        private set

    init {
        listenForMessages()
    }

    private fun listenForMessages() {
        viewModelScope.launch {
            listenChatMessagesUseCase(conversationId).collectLatest { messages ->
                uiState = uiState.copy(messages = messages)
            }
        }
    }

    fun sendMessage(messageText: String, senderId: String) {
        val message = Message(
            id = System.currentTimeMillis().toString(), // O genera un id único
            senderId = senderId,
            text = messageText
        )
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val result = sendMessageUseCase(conversationId, message)
            uiState = uiState.copy(isLoading = false)
            result.exceptionOrNull()?.let { exception ->
                uiState = uiState.copy(errorMessage = exception.message)
            }
        }
    }
}