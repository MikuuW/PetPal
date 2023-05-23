package com.mikuw.coupler.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Search

class Datasource_Firebase_Searches {
    private val db = FirebaseFirestore.getInstance()

    fun loadSearches(callback: (List<Search>) -> Unit) {
        db.collection("searches")
            .get()
            .addOnSuccessListener { result ->
                val searches = mutableListOf<Search>()
                for (document in result) {
                    val title = document.getString("title") ?: continue
                    val location = document.getString("city") ?: continue
                    val from = document.getDate("from") ?: continue
                    val to = document.getDate("to") ?: continue
                    val creator = document.getString("creator") ?: continue
                    val desc = document.getString("desc") ?: continue
                    val search = Search(title, location, from, to, creator, desc)
                    searches.add(Search(title, location, from, to, creator, desc))
                }
                callback(searches)
            }
    }


}