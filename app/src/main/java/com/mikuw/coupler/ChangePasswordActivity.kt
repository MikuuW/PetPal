package com.mikuw.coupler

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acivity_change_password)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Change Password"

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)

        val submit = findViewById<TextView>(R.id.btn_change_pw_submit)
        submit.setOnClickListener { changePassword() }
    }

    private fun changePassword() {
        val tv_password_new = findViewById<TextView>(R.id.tv_change_pw).text.toString()
        val tv_password_new_confirmation =
            findViewById<TextView>(R.id.tv_change_pw_confirm).text.toString()
        val tv_password_current = findViewById<TextView>(R.id.tv_change_pw_old).text.toString()

        // Passwort checken
        if (tv_password_new == tv_password_new_confirmation) {
            if (tv_password_new.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT)
                    .show()
                return
            }
        } else {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }


        val user = FirebaseAuth.getInstance().currentUser
        val credential =
            EmailAuthProvider.getCredential(user?.email.toString(), tv_password_current)

// Prompt the user to reauthenticate with their current password
        user?.reauthenticate(credential)
            ?.addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    // User has been successfully reauthenticated
                    // Proceed with changing the password

                    user.updatePassword(tv_password_new)
                        .addOnCompleteListener { passwordUpdateTask ->
                            if (passwordUpdateTask.isSuccessful) {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                                // Password updated successfully
                                Toast.makeText(
                                    this,
                                    "Password updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Password update failed
                                Toast.makeText(this, "Password update failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } else {
                    // Reauthentication failed
                    Toast.makeText(this, "Reauthentication failed", Toast.LENGTH_SHORT).show()
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