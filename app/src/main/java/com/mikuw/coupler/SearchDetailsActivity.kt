package com.mikuw.coupler

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mikuw.coupler.model.Pet
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import com.mikuw.coupler.model.Search
import kotlinx.coroutines.tasks.await

class SearchDetailsActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_details)
        val search = intent.getSerializableExtra("search") as? Search
        val title = search?.title
        val fromDate = search?.from
        val toDate = search?.to
        val creatorUid = search?.creator
        val location = search?.location
        val desc = search?.desc
        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupNavigationDrawer(this)

        // assign views
        val tv_title = findViewById<TextView>(R.id.tv_search_details_title)
        val tv_date = findViewById<TextView>(R.id.tv_search_details_date)
        val tv_name = findViewById<TextView>(R.id.iv_search_details_name)
        val tv_location = findViewById<TextView>(R.id.iv_search_details_location)
        val et_desc = findViewById<TextView>(R.id.et_search_details_desc)


        // Get the name of the Creator
        if (creatorUid != null) {
            getCreatorName(creatorUid,
                onSuccess = { fullName ->
                    // Handle successful retrieval of full name
                    tv_name.text = fullName
                },
                onFailure = { exception ->
                    // Handle failure case
                    println("Error retrieving user data: ${exception.message}")
                }
            )
        }

        // Count Days
        val days = if (fromDate != null && toDate != null) calculateDaysBetweenDates(fromDate, toDate) else null
        // assign text
        tv_title.text = title
        tv_date.text = formattedDate(fromDate) + " - " + formattedDate(toDate) + " \n$days day(s)"
        tv_location.text = location
        et_desc.text = desc

    }




    private fun getCreatorName(userId: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val firstname = documentSnapshot.getString("firstname")
                val lastname = documentSnapshot.getString("lastname")

                if (firstname != null && lastname != null) {
                    val fullName = "$firstname $lastname"

                    onSuccess(fullName)
                } else {
                    onFailure(Exception("Firstname or lastname is null"))
                }
            } else {
                onFailure(Exception("User document does not exist"))
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun formattedDate(date: Date?): String {
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun calculateDaysBetweenDates(fromDate: Date, toDate: Date): Long {
        val diffInMillies = toDate.time - fromDate.time
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
    }
}

