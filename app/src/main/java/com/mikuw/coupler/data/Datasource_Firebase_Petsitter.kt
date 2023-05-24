package com.mikuw.coupler.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Petsitter

class Datasource_Firebase_Petsitter {
    private val db = FirebaseFirestore.getInstance()
    fun loadPetsitter(callback: (List<Petsitter>) -> Unit) {
        db.collection("petsitters")
            .get()
            .addOnSuccessListener { result ->
                val petsitters = mutableListOf<Petsitter>()
                for (document in result) {
                    val firstname = document.getString("firstname") ?: continue
                    val lastname = document.getString("lastname") ?: continue
                    val email = document.getString("email") ?: continue
                    val imageUri = document.getString("imageUri") ?: continue
                    val postalcode = document.getString("postalcode") ?: continue
                    val street = document.getString("street") ?: continue
                    val streetNr = document.getString("streetNr") ?: continue
                    val city = document.getString("city") ?: continue
                    val desc = document.getString("desc") ?: continue
                    petsitters.add(Petsitter(firstname, lastname, email, imageUri, postalcode, street, streetNr, city, desc ))
                }
                callback(petsitters)
            }
    }
}