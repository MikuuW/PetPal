package com.mikuw.coupler

import EventsAdapter
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mikuw.coupler.data.Datasource_Firebase_Events
import com.mikuw.coupler.model.Pet
import java.io.File

class PetProfileActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_profile)

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        //TEST BURGER MENU
        val pet = intent.getSerializableExtra("pet") as? Pet
        val name = pet?.name
        val desc = pet?.desc


        // Assuming you have the imageUri string in a variable called imageUri
        val iv_petProfile_image = findViewById<ImageView>(R.id.iv_petProfile_image)
        //iv_petProfile_image.setImageURI(uri)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "$name's Profile"

        val tv_pet_desc = findViewById<TextView>(R.id.et_pet_desc)
        tv_pet_desc.text = desc
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        //TEST
        val dbRef = FirebaseStorage.getInstance().reference.child("images_pet/$userId/$name.jpg")

        val localFile = File.createTempFile("tmpImage", "jpg")
        dbRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            iv_petProfile_image.setImageBitmap(bitmap)
        }
        //TEST
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
