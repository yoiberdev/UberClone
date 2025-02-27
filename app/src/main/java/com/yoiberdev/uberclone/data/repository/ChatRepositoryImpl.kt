package com.yoiberdev.uberclone.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.yoiberdev.uberclone.data.source.firebase.FirebaseChatSource
import com.yoiberdev.uberclone.domain.model.Message

class ChatRepositoryImpl(
    firestore: FirebaseFirestore
) : ChatRepository {

    private val chatSource = FirebaseChatSource(firestore)

    override suspend fun sendMessage(conversationId: String, message: Message): Result<Unit> {
        return chatSource.sendMessage(conversationId, message)
    }

    override fun listenForMessages(conversationId: String) = chatSource.listenForMessages(conversationId)
}