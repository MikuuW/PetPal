package com.mikuw.coupler

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class tmpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmp)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "TMP Overview"


        val textViewMain = findViewById<TextView>(R.id.tv_main)
        val textViewLogin = findViewById<TextView>(R.id.tv_login)
        val textViewLogout = findViewById<TextView>(R.id.tv_logout)
        val textViewRegister = findViewById<TextView>(R.id.tv_register)
        val textViewEditProfile = findViewById<TextView>(R.id.tv_edit_profile)
        val textViewShowProfile = findViewById<TextView>(R.id.tv_show_profile)
        val textViewCreateMission = findViewById<TextView>(R.id.tv_create_mission)

        textViewMain.setOnClickListener {
            // Launch HomeActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        textViewLogin.setOnClickListener {
            // Launch LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        textViewRegister.setOnClickListener {
            // Launch RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        textViewEditProfile.setOnClickListener {
            // Launch com.mikuw.coupler.ProfileActivity
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        textViewShowProfile.setOnClickListener {
            // Launch com.mikuw.coupler.ProfileActivity
            val intent = Intent(this, ShowProfileActivity::class.java)
            startActivity(intent)
        }

        textViewCreateMission.setOnClickListener {
            // Launch com.mikuw.coupler.ProfileActivity
            val intent = Intent(this, CreateMissionActivity::class.java)
            startActivity(intent)
        }

        textViewLogout.setOnClickListener {
            // Launch com.mikuw.coupler.ProfileActivity
            FirebaseAuth.getInstance().signOut()
            println("User signed out")
            val intent = Intent(this, tmpActivity::class.java)
            startActivity(intent)
        }

        if (FirebaseAuth.getInstance().currentUser != null) {
            // User is signed in
            textViewMain.text = "Main (Logged in)"
        } else {
            // No user is signed in
            textViewMain.text = "Main (Logged out)"
        }
    }
}
