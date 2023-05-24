package com.mikuw.coupler

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.mikuw.coupler.model.Pet
import com.squareup.picasso.Picasso
import java.io.File

class PetProfileShowActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_profile_show)

        //BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)
        val navView: NavigationView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupNavigationDrawer(this)

        // Get values from the passed object
        val pet = intent.getSerializableExtra("pet") as? Pet
        val name = pet?.name
        val desc = pet?.desc
        val imageUri = pet?.imageUrl

        // Actionbar
        val actionBar = supportActionBar
        actionBar?.title = "$name's Profile"

        // Get the views
        val tv_pet_desc = findViewById<TextView>(R.id.et_pet_desc)

        // assign the desc value to the textview
        tv_pet_desc.text = desc


        displayImage(imageUri)

        val btn = findViewById<Button>(R.id.btn_pet_edit_button)
        if ((FirebaseAuth.getInstance().currentUser?.uid ?: "") != pet?.ownerId) {
            btn.visibility = View.INVISIBLE
        }

        btn.setOnClickListener() {
            val intent = intent
            intent.setClass(this, PetProfileEditActivity::class.java)
            intent.putExtra("pet", pet)
            startActivity(intent)
        }
    }

    private fun displayImage(uri: String?) {
        val iv_petProfile_image = findViewById<ImageView>(R.id.iv_petProfile_image)

        Picasso.get()
            .load(uri)
            .resize(400,400)
            .centerCrop()
            .into(iv_petProfile_image)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
