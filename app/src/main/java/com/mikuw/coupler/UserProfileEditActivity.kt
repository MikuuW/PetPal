package com.mikuw.coupler

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfileEditActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Edit Profile"

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        //TEST BURGER MENU

        getProfileInformation()

        val btn_edit_submit = findViewById<TextView>(R.id.btn_edit_profile_submit)
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


        var etv_edit_email = findViewById<TextView>(R.id.etv_edit_profile_email)
        var etv_edit_firstname = findViewById<TextView>(R.id.etv_edit_profile_firstname)
        var etv_edit_lastname = findViewById<TextView>(R.id.etv_edit_profile_lastname)
        var etv_edit_street = findViewById<TextView>(R.id.etv_edit_profile_street)
        var etv_edit_street_nr = findViewById<TextView>(R.id.etv_edit_profile_street_nr)
        var etv_edit_postalcode = findViewById<TextView>(R.id.etv_edit_profile_postalcode)
        var etv_edit_city = findViewById<TextView>(R.id.etv_edit_profile_city)


        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // retrieve the user's data
                val email = document.getString("email")
                val firstname = document.getString("firstname")
                val lastname = document.getString("lastname")
                val street = document.getString("street")
                val street_nr = document.getString("streetNr")
                val postalcode = document.getString("postalcode")
                val city = document.getString("city")
                // do something with the retrieved data


                etv_edit_email.text = email
                etv_edit_firstname.text = firstname
                etv_edit_lastname.text = lastname
                etv_edit_street.text = street
                etv_edit_street_nr.text = street_nr
                etv_edit_postalcode.text = postalcode
                etv_edit_city.text = city


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

        val email = findViewById<TextView>(R.id.etv_edit_profile_email).text.toString()
        val newFirstname = findViewById<TextView>(R.id.etv_edit_profile_firstname).text.toString().trim()
        val newLastname = findViewById<TextView>(R.id.etv_edit_profile_lastname).text.toString().trim()
        val newStreet = findViewById<TextView>(R.id.etv_edit_profile_street).text.toString().trim()
        val newStreetNr = findViewById<TextView>(R.id.etv_edit_profile_street_nr).text.toString().trim()
        val newPostalcode = findViewById<TextView>(R.id.etv_edit_profile_postalcode).text.toString().trim()
        val newCity = findViewById<TextView>(R.id.etv_edit_profile_city).text.toString().trim()


        //TODO: Nur Updaten wenn etwas geändert wurde

        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Create a new user object with the given email
        val user = hashMapOf(
            "email" to email,
            "firstname" to newFirstname,
            "lastname" to newLastname,
            "street" to newStreet,
            "streetNr" to newStreetNr,
            "postalcode" to newPostalcode,
            "city" to newCity
        )

        // Set the user object to a new document in the "users" collection with the user ID as the document ID
        db.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $userId")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
                Toast.makeText(this, "Something went wrong!\nTry again please!", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

