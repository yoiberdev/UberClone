package com.yoiberdev.uberclone.domain.usecase

import com.yoiberdev.uberclone.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import com.yoiberdev.uberclone.domain.model.Message

class ListenChatMessagesUseCase(private val repository: ChatRepository) {
    operator fun invoke(conversationId: String): Flow<List<Message>> {
        return repository.listenForMessages(conversationId)
    }
}