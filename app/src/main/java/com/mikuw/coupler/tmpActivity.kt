package com.mikuw.coupler

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class tmpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmp)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "TMP Overview"



        val textViewMain = findViewById<TextView>(R.id.tv_main)
        val textViewLogin = findViewById<TextView>(R.id.tv_login)
        val textViewRegister = findViewById<TextView>(R.id.tv_register)
        val textViewProfile = findViewById<TextView>(R.id.tv_profile)

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

        textViewProfile.setOnClickListener {
            // Launch ProfileActivity
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
