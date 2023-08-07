package com.mikuw.coupler.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Message

class Datasource_Firebase_Messages {
    private val db = FirebaseFirestore.getInstance()

    // load all messages for a user
    fun loadMessages(callback: (List<Message>) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        db.collection("messages")
            .whereEqualTo("receiver", currentUser?.uid)
            .get()
            .addOnSuccessListener { result ->
                val messages = mutableListOf<Message>()
                for (document in result) {
                    val title = document.getString("title") ?: continue
                    val sender = document.getString("sender") ?: continue
                    val receiver = document.getString("receiver") ?: continue
                    val content = document.getString("content") ?: continue
                    val timestamp = document.getLong("timestamp") ?: continue
                    val isRead = document.getBoolean("isRead") ?: continue
                    val msg = Message(title, sender, receiver, content, timestamp, isRead)
                    messages.add(msg)
                }
                callback(messages)
            }
    }
}