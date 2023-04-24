package com.mikuw.coupler

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class ShowProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Show Profile"

        // Display the email in a TextView
        getProfileInformation()



    }

    private fun getProfileInformation() {

        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null) {
            Log.d(TAG, "No user is logged in")
            return
        }
        val userRef = db.collection("users").document(currentUser!!.uid)


        var tv_show_email = findViewById<TextView>(R.id.tv_show_email)


        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // retrieve the user's data
                val email = document.getString("email")
                // do something with the retrieved data
                println("In der Funktion...")
                println(email)
                tv_show_email.setText(email)
            } else {
                // handle the case when the document does not exist
                println("No such document")
            }
        }.addOnFailureListener { exception ->
            // handle any exceptions that occur
            println("Error getting documents: $exception")
        }


    }
}