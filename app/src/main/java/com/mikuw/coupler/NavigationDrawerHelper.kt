// NavigationDrawerHelper.kt

package com.mikuw.coupler

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.mikuw.coupler.R

fun setupNavigationDrawer(activity: Activity) {
    val drawerLayout : DrawerLayout = activity.findViewById(R.id.drawer_layout)
    val navView : NavigationView = activity.findViewById(R.id.nav_view)

    val toggle = ActionBarDrawerToggle(activity, drawerLayout, R.string.open, R.string.close)
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    activity.actionBar?.setDisplayHomeAsUpEnabled(true)

    navView.setNavigationItemSelectedListener {
        when(it.itemId) {
            R.id.nav_main -> {
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
            }
            R.id.nav_login -> {
                val intent = Intent(activity, LoginActivity::class.java)
                activity.startActivity(intent)
            }
            R.id.nav_register -> {
                val intent = Intent(activity, RegisterActivity::class.java)
                activity.startActivity(intent)
            }
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
            }
            R.id.nav_my_profile -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(activity, ShowProfileActivity::class.java)
                activity.startActivity(intent)
            }
            R.id.nav_edit_profile -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(activity, EditProfileActivity::class.java)
                activity.startActivity(intent)
            }

        }
        true
    }
}
