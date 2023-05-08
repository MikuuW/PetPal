package com.mikuw.coupler.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Event

class Datasource_Firebase_Events {
    private val db = FirebaseFirestore.getInstance()

    fun loadEvents(callback: (List<Event>) -> Unit) {
        db.collection("searches")
            .get()
            .addOnSuccessListener { result ->
                val events = mutableListOf<Event>()
                val locations = mutableListOf<Event>()
                for (document in result) {
                    val name = document.getString("title") ?: continue
                    val location = document.getString("city") ?: continue
                    val from = document.getDate("from") ?: continue
                    val to = document.getDate("to") ?: continue
                    val event = Event(name, location, from, to)
                    events.add(Event(name, location, from, to))
                    println(event)
                }
                callback(events)
            }
    }
}