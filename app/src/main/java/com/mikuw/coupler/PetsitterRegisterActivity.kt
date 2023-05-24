package com.mikuw.coupler

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PetsitterRegisterActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_petsitter_register)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Become a Petsitter"

        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)

        // Ab here

        val btn = findViewById<Button>(R.id.btn_become_petsitter_submit)

        btn.setOnClickListener() {
            createPetsitterInFirestore()
        }
        val intent = intent
        intent.setClass(this, MainActivity::class.java)
        startActivity(intent)

    }

    private fun createPetsitterInFirestore() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val city = document.data?.get("city").toString()
                        val email = document.data?.get("email").toString()
                        val firstname = document.data?.get("firstname").toString()
                        val lastname = document.data?.get("lastname").toString()
                        val image_url = document.data?.get("image_url").toString()
                        val postalcode = document.data?.get("postalcode").toString()
                        val street = document.data?.get("street").toString()
                        val streetnr = document.data?.get("streetnr").toString()
                        val petsitter = hashMapOf(
                            "city" to city,
                            "email" to email,
                            "firstname" to firstname,
                            "lastname" to lastname,
                            "image_url" to image_url,
                            "postalcode" to postalcode,
                            "street" to street,
                            "streetnr" to streetnr
                        )
                        db.collection("petsitters").document(uid).set(petsitter)
                    }
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
