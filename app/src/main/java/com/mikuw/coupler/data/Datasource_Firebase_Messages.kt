package com.mikuw.coupler.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Message

class Datasource_Firebase_Messages {
    private val db = FirebaseFirestore.getInstance()

    fun loadMessages(callback: (List<Message>) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        db.collection("messages")
            .whereEqualTo("receiver", currentUser?.uid)
            .get()
            .addOnSuccessListener { result ->
                val messages = mutableListOf<Message>()
                for (document in result) {
                    val sender = document.getString("sender") ?: continue
                    val receiver = document.getString("receiver") ?: continue
                    val content = document.getString("content") ?: continue
                    val timestamp = document.getLong("timestamp") ?: continue
                    val msg = Message(sender, receiver, content, timestamp)
                    messages.add(msg)
                    println("loadMessages: " + messages)
                }
                callback(messages)
                println("callack(messages) called" + messages)
            }
    }
    fun getSampleMessages(): List<Message> {
        return listOf(
            Message("Sender 1", "Receiver 1","Hello", System.currentTimeMillis()),
            Message("Sender 2", "Receiver 2", "Hi", System.currentTimeMillis()),
            Message("Sender 3", "Receiver 3","How are you?", System.currentTimeMillis())
        )
    }
}