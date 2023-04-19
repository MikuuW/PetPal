package com.mikuw.coupler

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

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
            registerUser(email, password, passwordConfirmation)
            createUserinFirestore(email)
        }
    }

    private fun registerUser(email: String, password: String, passwordConfirmation: String) {
        val auth = FirebaseAuth.getInstance()

        // Wenn ein Feld leer ist oder Passwörter nicht übereinstimmen
        if (email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show()
        } else if (password != passwordConfirmation) {
            Toast.makeText(this, "Passwords does not match!", Toast.LENGTH_SHORT).show()
        }

        try {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful, you can get the user information from the AuthResult object
                    val user = task.result?.user
                    Log.d(TAG, "User account created with email: ${user?.email}")
                    Toast.makeText(
                        this, "User created successful", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Login failed, handle specific types of exceptions
                    when (val exception = task.exception) {
                        is FirebaseAuthWeakPasswordException -> {
                            Toast.makeText(
                                this, "The password is too weak.", Toast.LENGTH_SHORT
                            ).show()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(
                                this, "The email address is not valid.", Toast.LENGTH_SHORT
                            ).show()
                        }
                        is FirebaseAuthUserCollisionException -> {
                            Toast.makeText(
                                this, "The email address is already in use.", Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            // Unknown error occurred, display a generic error message
                            Toast.makeText(
                                this, "An error occurred: ${exception?.message}", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user account", e)
            Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createUserinFirestore(email: String) {
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()

        // Create a new user object with the given email
        val user = hashMapOf(
            "email" to email
        )

        // Add the user object to a new document in the "users" collection
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

} //end
