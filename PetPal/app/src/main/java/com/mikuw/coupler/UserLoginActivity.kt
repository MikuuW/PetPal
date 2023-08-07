package com.mikuw.coupler

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class UserLoginActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Login"

        // BURGER MENU / NAVIGATION DRAWER
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)

        val btn = findViewById<TextView>(R.id.tv_register_here)

        btn.setOnClickListener() {
            val intent = Intent(this, UserRegisterActivity::class.java)
            startActivity(intent)
        }

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
                            Toast.makeText(
                                baseContext, "Logged in successfully.",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Sign in success, update UI with the signed-in user's information
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext, "Username or Password is incorrect",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
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
