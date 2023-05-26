package com.mikuw.coupler

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class tmpActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmp)

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        //TEST BURGER MENU

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
            val intent = Intent(this, UserLoginActivity::class.java)
            startActivity(intent)
        }

        // Register Activity
        val tv_register = findViewById<TextView>(R.id.tv_register)
        tv_register.setOnClickListener {
            val intent = Intent(this, UserRegisterActivity::class.java)
            startActivity(intent)
        }

        // Edit Profile Activity
        val tv_editProfile = findViewById<TextView>(R.id.tv_edit_profile)
        tv_editProfile.setOnClickListener {
            val intent = Intent(this, UserProfileEditActivity::class.java)
            startActivity(intent)
        }

        // Show Profile Activity
        val tv_showProfile = findViewById<TextView>(R.id.tv_show_profile)
        tv_showProfile.setOnClickListener {
            val intent = Intent(this, UserProfileShowActivity::class.java)
            startActivity(intent)
        }

        // Create Mission Activity
        val tv_createMission = findViewById<TextView>(R.id.tv_create_mission)
        tv_createMission.setOnClickListener {
            val intent = Intent(this, SearchCreateActivity::class.java)
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
            val intent = Intent(this, PetsListActivity::class.java)
            startActivity(intent)
        }

        // Add Pets
        val tv_add_pets = findViewById<TextView>(R.id.tv_add_pets)
        tv_add_pets.setOnClickListener {
            val intent = Intent(this, PetAddActivity::class.java)
            startActivity(intent)
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
