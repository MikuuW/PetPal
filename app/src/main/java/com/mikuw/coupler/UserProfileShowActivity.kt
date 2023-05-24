package com.mikuw.coupler

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class UserProfileShowActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_show)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Your Profile"

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        //TEST BURGER MENU
        // Display the email in a TextView
        getProfileInformation()

        val btn_edit_profile = findViewById<Button>(R.id.btn_edit_profile)
        btn_edit_profile.setOnClickListener {
            val intent = Intent(this, UserProfileEditActivity::class.java)
            startActivity(intent)
        }

        val btn_change_password = findViewById<TextView>(R.id.btn_change_password)
        btn_change_password.setOnClickListener() {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getProfileInformation() {
        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.d(TAG, "No user is logged in")
            return
        }
        val userRef = db.collection("users").document(currentUser!!.uid)
        var tv_show_firstname = findViewById<TextView>(R.id.tv_show_profile_firstname)
        var iv_profile_show_image = findViewById<ImageView>(R.id.iv_profile_show_image)
        var tv_show_street = findViewById<TextView>(R.id.tv_show_profile_street)
        var tv_show_postalcode = findViewById<TextView>(R.id.tv_show_profile_postalcode)
        var tv_show_email = findViewById<TextView>(R.id.tv_show_profile_email)
        var tv_show_desc = findViewById<TextView>(R.id.tv_show_profile_desc)

        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // retrieve the user's data
                val firstname = document.getString("firstname")
                val lastname = document.getString("lastname")
                val imageUri = document.getString("imageUri")
                var street = document.getString("street")
                var streetNr = document.getString("streetNr")
                var postalcode = document.getString("postalcode")
                var city = document.getString("city")
                val email = document.getString("email")
                val desc = document.getString("desc")

                // do something with the retrieved data
                tv_show_firstname.text = firstname + " " + lastname
                tv_show_street.text = street + " " + streetNr
                tv_show_postalcode.text = postalcode + " " + city
                tv_show_email.text = email
                tv_show_desc.text = desc

                if (!imageUri.isNullOrEmpty()) {
                    Picasso.get()
                        .load(imageUri)
                        .resize(200, 200)
                        .centerCrop()
                        .into(iv_profile_show_image)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}