package com.mikuw.coupler

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mikuw.coupler.model.Petsitter
import com.mikuw.coupler.model.Search
import com.squareup.picasso.Picasso

class PetsitterDetailsActivity : AppCompatActivity(){
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_petsitter_details)

        // Get Petsitter values
        val petsitter = intent.getSerializableExtra("petsitter") as? Petsitter
        val firstname = petsitter?.firstname
        val lastname = petsitter?.lastname
        val postalcode = petsitter?.postalcode
        val city = petsitter?.city
        val street = petsitter?.street
        val streetNr = petsitter?.streetNr
        val imageUri = petsitter?.imageUri
        val desc = petsitter?.desc

        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupNavigationDrawer(this)

        // Format values
        val name = "$firstname $lastname"
        val adress = "$street $streetNr\n$postalcode $city"

        // Get views

        // Call functions
        setValues(name, adress, desc)
        displayImage(imageUri.toString())
    }

    private fun displayImage(uri: String) {
        val iv_image = findViewById<ImageView>(R.id.iv_petsitter_details_image)

        Picasso.get()
            .load(uri)
            .resize(400,400)
            .centerCrop()
            .into(iv_image)
    }

    private fun setValues(name : String, adress : String, desc : String?) {
        val tv_name = findViewById<TextView>(R.id.tv_petsitter_details_name)
        val tv_adress = findViewById<TextView>(R.id.tv_petsitter_details_adress)
        val tv_desc = findViewById<TextView>(R.id.tv_petsitter_details_desc)

        tv_name.text = name
        tv_adress.text = adress
        tv_desc.text = desc
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}