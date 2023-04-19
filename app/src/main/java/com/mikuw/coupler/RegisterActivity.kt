package com.mikuw.coupler

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

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
        val passwordConfirmationTextView =
            findViewById<TextView>(R.id.etv_register_passwordConfirmation)
        val registerButton = findViewById<Button>(R.id.button_register)

        registerButton.setOnClickListener {
            val email = emailTextView.text.toString()
            val password = passwordTextView.text.toString()
            val passwordConfirmation = passwordConfirmationTextView.text.toString()


            // Wenn ein Feld leer ist oder Passwörter nicht übereinstimmen
            if (email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show()
            } else if (password != passwordConfirmation) {
                Toast.makeText(this, "Passwords does not match!", Toast.LENGTH_SHORT).show()
            }


            val auth = FirebaseAuth.getInstance()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        // User creation failed
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }



        }


    }
}
