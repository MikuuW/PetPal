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


        // Main Activity
        val tv_main = findViewById<TextView>(R.id.tv_main)
        tv_main.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // Check if someone is logged in
        if (FirebaseAuth.getInstance().currentUser != null) {
            // User is signed in
            tv_main.text = "Main (Logged in)"
        } else {
            // No user is signed in
            tv_main.text = "Main (Logged out)"
        }

        // Login Activity
        val tv_login = findViewById<TextView>(R.id.tv_login)
        tv_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Register Activity
        val tv_register = findViewById<TextView>(R.id.tv_register)
        tv_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Edit Profile Activity
        val tv_editProfile = findViewById<TextView>(R.id.tv_edit_profile)
        tv_editProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // Show Profile Activity
        val tv_showProfile = findViewById<TextView>(R.id.tv_show_profile)
        tv_showProfile.setOnClickListener {
            val intent = Intent(this, ShowProfileActivity::class.java)
            startActivity(intent)
        }

        // Create Mission Activity
        val tv_createMission = findViewById<TextView>(R.id.tv_create_mission)
        tv_createMission.setOnClickListener {
            val intent = Intent(this, CreateMissionActivity::class.java)
            startActivity(intent)
        }

        // Logout Activity
        val tv_logout = findViewById<TextView>(R.id.tv_logout)
        tv_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, tmpActivity::class.java)
            startActivity(intent)
        }

        // MyPets Activity
        val tv_my_pets = findViewById<TextView>(R.id.tv_my_pets)
        tv_my_pets.setOnClickListener {
            val intent = Intent(this, MyPetsActivity::class.java)
            startActivity(intent)
        }

        // Add Pets
        val tv_add_pets = findViewById<TextView>(R.id.tv_add_pets)
        tv_add_pets.setOnClickListener {
            val intent = Intent(this, AddPetsActivity::class.java)
            startActivity(intent)
        }
    }
}
