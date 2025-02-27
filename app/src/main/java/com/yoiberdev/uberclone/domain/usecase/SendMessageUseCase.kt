package com.yoiberdev.uberclone.domain.usecase

import com.yoiberdev.uberclone.data.repository.ChatRepository
import com.yoiberdev.uberclone.domain.model.Message

class SendMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(conversationId: String, message: Message): Result<Unit> {
        return repository.sendMessage(conversationId, message)
    }
}