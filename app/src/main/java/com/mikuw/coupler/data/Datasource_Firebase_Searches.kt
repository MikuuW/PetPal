package com.mikuw.coupler.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Search

class Datasource_Firebase_Searches {
    private val db = FirebaseFirestore.getInstance()

    fun loadEvents(callback: (List<Search>) -> Unit) {
        db.collection("searches")
            .get()
            .addOnSuccessListener { result ->
                val events = mutableListOf<Search>()
                for (document in result) {
                    val title = document.getString("title") ?: continue
                    val location = document.getString("city") ?: continue
                    val from = document.getDate("from") ?: continue
                    val to = document.getDate("to") ?: continue
                    val creator = document.getString("creator") ?: continue
                    val desc = document.getString("desc") ?: continue
                    val event = Search(title, location, from, to, creator, desc)
                    events.add(Search(title, location, from, to, creator, desc))
                }
                callback(events)
            }
    }
}