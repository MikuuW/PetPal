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







/*
package com.mikuw.coupler.data

import com.mikuw.coupler.R
import com.mikuw.coupler.model.Affirmation

class com.mikuw.coupler.data.Datasource {
    fun loadAffirmations(): List<Affirmation> {
        return listOf<Affirmation>(
            Affirmation(R.string.affirmation1),
            Affirmation(R.string.affirmation2),
            Affirmation(R.string.affirmation3),
            Affirmation(R.string.affirmation4),
            Affirmation(R.string.affirmation5),
            Affirmation(R.string.affirmation6),
            Affirmation(R.string.affirmation7),
            Affirmation(R.string.affirmation8),
            Affirmation(R.string.affirmation9),
            Affirmation(R.string.affirmation10),
            Affirmation(R.string.affirmation11)
        )
    }
}
 */