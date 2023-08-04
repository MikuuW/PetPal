package com.mikuw.coupler.data

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Pet

class Datasource_Firebase_Pets {
    private val db = FirebaseFirestore.getInstance()

    fun loadPets(callback: (List<Pet>) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        db.collection("pets")
            .whereEqualTo("owner", currentUser?.uid)
            .get()
            .addOnSuccessListener { result ->
                val pets = mutableListOf<Pet>()
                for (document in result) {
                    val name = document.getString("name") ?: continue
                    val desc = document.getString("desc") ?: continue
                    val owner = document.getString("owner") ?: continue
                    val imageUri = document.getString("imageUri") ?: continue
                    val pet = Pet(name, desc, owner, imageUri)
                    pets.add(pet)
                }
                callback(pets)
            }
    }

    fun loadPetsinSearch(docId: String, callback: (List<Pet>) -> Unit) {
        if (docId != null) {
            val db = FirebaseFirestore.getInstance()
            val documentRef = db.collection("searches").document(docId)

            documentRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val petList = documentSnapshot.get("pets") as? List<HashMap<String, Any>>

                    if (petList != null) {
                        val pets = petList.mapNotNull { petMap ->
                            val desc = petMap["desc"] as? String
                            val imageUrl = petMap["imageUrl"] as? String
                            val name = petMap["name"] as? String
                            val ownerId = petMap["ownerId"] as? String


                            if (desc != null && imageUrl != null && name != null && ownerId != null) {
                                Pet(name, desc, ownerId, imageUrl)
                            } else {
                                null
                            }
                        }
                        println(pets)
                        callback(pets)
                    } else {
                        Log.d(TAG, "No pets found in the document")
                        callback(emptyList()) // Return an empty list if no pets are found
                    }
                } else {
                    Log.d(TAG, "Document does not exist")
                    callback(emptyList()) // Return an empty list if the document does not exist
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting document: $exception")
                callback(emptyList()) // Return an empty list in case of failure
            }
        } else {
            callback(emptyList()) // Return an empty list if the document ID is null
        }
    }

}
