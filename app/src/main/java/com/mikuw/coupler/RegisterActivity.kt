package com.mikuw.coupler

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Register"

        // Do any additional setup for your activity here
        // Variablen zuweisen
        val emailTextView = findViewById<TextView>(R.id.etv_register_email)
        val passwordTextView = findViewById<TextView>(R.id.etv_register_password)
        val passwordConfirmationTextView = findViewById<TextView>(R.id.etv_register_passwordConfirmation)
        val registerButton = findViewById<Button>(R.id.button_register)

        registerButton.setOnClickListener {
            val email = emailTextView.text.toString()
            val password = passwordTextView.text.toString()
            val passwordConfirmation = passwordConfirmationTextView.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && passwordConfirmation.isNotEmpty()) {
                // This code will be executed when the button is clicked and all the fields are not empty
                println(email)
                println(password)
                println(passwordConfirmation)
            } else {
                // Handle the case when any of the fields are empty
                // For example, show an error message to the user
                println("Please fill all the fields.")
            }
        }



    }
}
