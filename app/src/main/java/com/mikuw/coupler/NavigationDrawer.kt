// NavigationDrawer.kt

package com.mikuw.coupler

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun setupNavigationDrawer(activity: Activity) {
    val drawerLayout: DrawerLayout = activity.findViewById(R.id.tv_edit_image)
    val navView: NavigationView = activity.findViewById(R.id.nav_view)

    val toggle = ActionBarDrawerToggle(activity, drawerLayout, R.string.open, R.string.close)
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    activity.actionBar?.setDisplayHomeAsUpEnabled(true)

    val btn_home = navView.getHeaderView(0).findViewById<ImageButton>(R.id.ib_nav_home)
    val btn_register = navView.getHeaderView(0).findViewById<ImageButton>(R.id.ib_nav_register)
    val btn_settings = navView.getHeaderView(0).findViewById<ImageButton>(R.id.ib_nav_settings)

    btn_home.setOnClickListener() {
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
    }

    btn_register.setOnClickListener() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        } else {
            val intent = Intent(activity, UserLoginActivity::class.java)
            activity.startActivity(intent)
        }

    }

    btn_settings.setOnClickListener() {
        val intent = Intent(activity, UserProfileShowActivity::class.java)
        activity.startActivity(intent)
    }


    navView.setNavigationItemSelectedListener {
        when (it.itemId) {

            R.id.nav_pets -> {
                val intent = Intent(activity, PetsListActivity::class.java)
                activity.startActivity(intent)

            }
            R.id.nav_add_pets -> {
                val intent = Intent(activity, PetAddActivity::class.java)
                activity.startActivity(intent)
            }
            R.id.nav_create_mission -> {
                val intent = Intent(activity, SearchCreateActivity::class.java)
                activity.startActivity(intent)
            }
            R.id.nav_become_petsitter -> {
                val intent = Intent(activity, PetsitterRegisterActivity::class.java)
                activity.startActivity(intent)
            }
            R.id.nav_my_searches -> {
                val intent = Intent(activity, SearchesListActivity::class.java)
                activity.startActivity(intent)
            }
            R.id.nav_messages -> {
                val intent = Intent(activity, MessagesListActivity::class.java)
                activity.startActivity(intent)
            }
            R.id.nav_imprint -> {
                val intent = Intent(activity, ImprintActivity::class.java)
                activity.startActivity(intent)
            }
        }
        true
    }
    getUserNameAndEmail(navView)
    handleNotLoggedInUser(navView)
}

fun handleNotLoggedInUser(navView: NavigationView) {
    val btn_register = navView.getHeaderView(0).findViewById<ImageButton>(R.id.ib_nav_register)
    val tv_show_name = navView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_name)


    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
    if (isLoggedIn) {
        btn_register.setImageResource(R.drawable.baseline_logout_24)
    } else {
        tv_show_name.text = "PetPal"
        tv_show_name.textSize = 30F

        val navCreatePet = navView.menu.findItem(R.id.nav_add_pets)
        navCreatePet?.isVisible = false
        val navCreateSearch = navView.menu.findItem(R.id.nav_create_mission)
        navCreateSearch?.isVisible = false
        val navMessagesItem = navView.menu.findItem(R.id.nav_messages)
        navMessagesItem?.isVisible = false
        val navBecomePetsitter = navView.menu.findItem(R.id.nav_become_petsitter)
        navBecomePetsitter?.isVisible = false


    }
}

fun getUserNameAndEmail(navView: NavigationView) {
    val db = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    val userRef = currentUser?.let { db.collection("users").document(it.uid) }

    val tv_show_email = navView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_email)
    val tv_show_name = navView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_name)



    userRef?.get()?.addOnSuccessListener { document ->
        if (document != null && document.exists()) {
            // retrieve the user's data
            val email = document.getString("email")
            val firstname = document.getString("firstname")
            val lastname = document.getString("lastname")
            val name = firstname + " " + lastname
            // do something with the retrieved data
            tv_show_name.text = name
            tv_show_email.text = email
        } else {
            // handle the case when the document does not exist
            println("No such document")
        }
    }?.addOnFailureListener { exception ->
        // handle any exceptions that occur
        println("Error getting documents: $exception")
    }
}
