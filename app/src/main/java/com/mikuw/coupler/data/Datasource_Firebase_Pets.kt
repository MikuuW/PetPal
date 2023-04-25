package com.mikuw.coupler.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Pet

class Datasource_Firebase_Pets {
    private val db = FirebaseFirestore.getInstance()

    fun loadPets(callback: (List<Pet>) -> Unit) {
        db.collection("pets")
            .get()
            .addOnSuccessListener { result ->
                val pets = mutableListOf<Pet>()
                for (document in result) {
                    val name = document.getString("name") ?: continue
                    val desc = document.getString("desc") ?: continue
                    val owner = document.getString("owner") ?: continue
                    val type = document.getString("type") ?: continue
                    val pet = Pet(name, desc, owner, type)
                    pets.add(Pet(name, desc, owner, type))
                    println(pet)
                }
                callback(pets)
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