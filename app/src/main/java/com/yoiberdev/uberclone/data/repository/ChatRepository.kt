package com.yoiberdev.uberclone.data.repository

import com.yoiberdev.uberclone.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun sendMessage(conversationId: String, message: Message): Result<Unit>
    fun listenForMessages(conversationId: String): Flow<List<Message>>
}