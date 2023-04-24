package com.mikuw.coupler

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Edit Profile"


        getProfileInformation()

        val btn_edit_submit = findViewById<TextView>(R.id.btn_edit_submit)
        btn_edit_submit.setOnClickListener {
            updateUserOnSubmit()
        }

    }

    private fun getProfileInformation() {

        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.d(ContentValues.TAG, "No user is logged in")
            return
        }
        val userRef = db.collection("users").document(currentUser.uid)


        var etv_edit_email = findViewById<TextView>(R.id.etv_edit_email)
        var etv_edit_firstname = findViewById<TextView>(R.id.etv_edit_firstname)
        var etv_edit_lastname = findViewById<TextView>(R.id.etv_edit_lastname)
        var etv_edit_postalcode = findViewById<TextView>(R.id.etv_edit_postalcode)
        var etv_edit_city = findViewById<TextView>(R.id.etv_edit_city)


        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // retrieve the user's data
                val email = document.getString("email")
                val firstname = document.getString("firstname")
                val lastname = document.getString("lastname")
                val postalcode = document.getString("postalcode")
                val city = document.getString("city")
                // do something with the retrieved data


                etv_edit_email.setText(email)


                if (firstname != null) {
                    etv_edit_firstname.setText(firstname)
                }
                if (lastname != null) {
                    etv_edit_lastname.setText(lastname)
                }
                if (postalcode != null) {
                    etv_edit_postalcode.setText(postalcode)
                }
                if (city != null) {
                    etv_edit_city.setText(city)
                }


            } else {
                // handle the case when the document does not exist
                println("No such document")
            }
        }.addOnFailureListener { exception ->
            // handle any exceptions that occur
            println("Error getting documents: $exception")
        }


    }

    private fun updateUserOnSubmit() {

        val email = findViewById<TextView>(R.id.etv_edit_email).text.toString()
        val newFirstname = findViewById<TextView>(R.id.etv_edit_firstname).text.toString().trim()
        val newLastname = findViewById<TextView>(R.id.etv_edit_lastname).text.toString().trim()
        val newPostalcode = findViewById<TextView>(R.id.etv_edit_postalcode).text.toString().trim()
        val newCity = findViewById<TextView>(R.id.etv_edit_city).text.toString().trim()


        //TODO: Nur Updaten wenn etwas geändert wurde

        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // Create a new user object with the given email
        val user = hashMapOf(
            "email" to email,
            "firstname" to newFirstname,
            "lastname" to newLastname,
            "postalcode" to newPostalcode,
            "city" to newCity
        )

        // Set the user object to a new document in the "users" collection with the user ID as the document ID
        db.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $userId")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }
}

