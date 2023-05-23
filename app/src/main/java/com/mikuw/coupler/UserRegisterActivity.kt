package com.mikuw.coupler

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class UserRegisterActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Register"

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        //TEST BURGER MENU
        // Do any additional setup for your activity here
        // Variablen zuweisen
        val email = findViewById<TextView>(R.id.etv_register_email)
        val firstname = findViewById<TextView>(R.id.etv_register_firstname)
        val lastname = findViewById<TextView>(R.id.etv_register_lastname)
        val street = findViewById<TextView>(R.id.etv_register_street)
        val streetNr = findViewById<TextView>(R.id.etv_register_street_nr)
        val postalcode = findViewById<TextView>(R.id.etv_register_postalcode)
        val city = findViewById<TextView>(R.id.etv_register_city)
        val password = findViewById<TextView>(R.id.etv_register_password)
        val passwordConfirmation =
            findViewById<TextView>(R.id.etv_register_passwordConfirmation)
        val registerButton = findViewById<Button>(R.id.button_register)

        registerButton.setOnClickListener {
            registerUser(
                firstname.text.toString(),
                lastname.text.toString(),
                street.text.toString(),
                streetNr.text.toString(),
                postalcode.text.toString(),
                city.text.toString(),
                email.text.toString(),
                password.text.toString(),
                passwordConfirmation.text.toString()
            )
        }
    }

    private fun registerUser(
        firstname: String,
        lastname: String,
        street: String?,
        streetNr: String?,
        postalcode: String?,
        city: String?,
        email: String,
        password: String,
        passwordConfirmation: String
    ) {
        val auth = FirebaseAuth.getInstance()

        // Wenn ein Feld leer ist oder Passwörter nicht übereinstimmen
        if (email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show()
        } else if (password != passwordConfirmation) {
            Toast.makeText(this, "Passwords does not match!", Toast.LENGTH_SHORT).show()
        }

        // Get reference to the AGB checkbox
        val agbCheckBox = findViewById<CheckBox>(R.id.cb_agb)

        // Check if AGB checkbox is checked
        if (!agbCheckBox.isChecked) {
            Toast.makeText(this, "You must accept our AGBs", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful, you can get the user information from the AuthResult object
                    var userId = FirebaseAuth.getInstance().currentUser?.uid
                    createUserInFirestore(
                        firstname,
                        lastname,
                        street,
                        streetNr,
                        postalcode,
                        city,
                        email,
                        userId ?: ""
                    )
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

    private fun createUserInFirestore(
        firstname: String,
        lastname: String,
        street: String?,
        streetNr: String?,
        postalcode: String?,
        city: String?,
        email: String,
        userId: String
    ) {
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()

        // Create a new user object with the given email
        val user = hashMapOf(
            "firstname" to firstname,
            "lastname" to lastname,
            "street" to street,
            "streetNr" to streetNr,
            "postalcode" to postalcode,
            "city" to city,
            "email" to email
        )

        // Set the user object to a new document in the "users" collection with the user ID as the document ID
        db.collection("users")
            .document(userId)
            .set(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: $userId")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

} //end
