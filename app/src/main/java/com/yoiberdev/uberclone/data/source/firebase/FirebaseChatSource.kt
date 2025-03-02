package com.yoiberdev.uberclone.data.source.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.yoiberdev.uberclone.domain.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseChatSource(private val firestore: FirebaseFirestore) {

    private val chatCollection = firestore.collection("chats") // Puedes organizarlo por conversaciones

    // Función para enviar un mensaje
    suspend fun sendMessage(conversationId: String, message: Message): Result<Unit> {
        return try {
            chatCollection.document(conversationId)
                .collection("messages")
                .document(message.id)
                .set(message)
                .addOnSuccessListener {  }
                .addOnFailureListener { throw it }
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función para escuchar mensajes en tiempo real
    fun listenForMessages(conversationId: String): Flow<List<Message>> = callbackFlow {
        val subscription = chatCollection.document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.toObjects(Message::class.java) ?: emptyList()
                trySend(messages)
            }
        awaitClose { subscription.remove() }
    }
}