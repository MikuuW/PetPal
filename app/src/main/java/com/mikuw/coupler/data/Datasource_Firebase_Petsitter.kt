package com.mikuw.coupler.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Petsitter

class Datasource_Firebase_Petsitter {
    private val db = FirebaseFirestore.getInstance()

    fun loadPetsitter(callback: (List<Petsitter>) -> Unit) {
        db.collection("Petsitter")
            .get()
            .addOnSuccessListener { result ->
                val petsitters = mutableListOf<Petsitter>()
                for (document in result) {
                    val name = document.getString("name") ?: continue
                    val city = document.getString("city") ?: continue
                    petsitters.add(Petsitter(name, city ))
                    println("Petsitter: $name, $city")
                }
                callback(petsitters)
            }
    }
}