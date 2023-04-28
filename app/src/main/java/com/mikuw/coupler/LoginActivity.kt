package com.mikuw.coupler

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Login"

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
        val emailTextView = findViewById<TextView>(R.id.etv_login_email)
        val passwordTextView = findViewById<TextView>(R.id.etv_login_password)
        val loginButton = findViewById<Button>(R.id.button_login)

        loginButton.setOnClickListener {
            val email = emailTextView.text.toString()
            val password = passwordTextView.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val auth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(email, password)
            } else {
                // Handle the case when any of the fields are empty
                // For example, show an error message to the user
                println("Please fill all the fields.")
            }
        }

        loginButton.setOnClickListener() {
            val email = emailTextView.text.toString()
            val password = passwordTextView.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val auth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            println("signInWithEmail:success")
                            val user = auth.currentUser
                            val intent = Intent(this, tmpActivity::class.java)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            println("signInWithEmail:failure")
                            println(task.exception)
                        }
                    }
            } else {
                // Handle the case when any of the fields are empty
                // For example, show an error message to the user
                println("Please fill all the fields.")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
