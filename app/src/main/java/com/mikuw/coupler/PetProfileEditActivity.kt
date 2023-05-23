package com.mikuw.coupler

import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
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
import java.io.File

class PetProfileEditActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_profile_edit)

        // BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupNavigationDrawer(this)

        val pet = intent.getSerializableExtra("pet") as? Pet
        val name = pet?.name
        val desc = pet?.desc

        // Actionbar
        val actionBar = supportActionBar
        actionBar?.title = "$name's Profile"

        val tv_pet_desc = findViewById<TextView>(R.id.et_pet_desc)
        tv_pet_desc.text = desc

        // Display Image
        displayImage(name)

        val btn = findViewById<Button>(R.id.btn_pet_submit)

        btn.setOnClickListener() {
            val newDesc = tv_pet_desc.text.toString()
            updatePetInFirestore(name, newDesc)
            val intent = intent
            intent.setClass(this, PetsShowActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updatePetInFirestore(name: String?, desc: String?) {
        val owner = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        db.collection("pets")
            .whereEqualTo("owner", owner)
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val petId = documentSnapshot.id
                    db.collection("pets")
                        .document(petId)
                        .update("desc", desc)
                        .addOnSuccessListener {
                            // Update successful
                            Log.d(TAG, "Pet updated in Firestore")
                        }
                        .addOnFailureListener { e ->
                            // Handle failure
                            Log.e(TAG, "Error updating pet in Firestore", e)
                        }
                } else {
                    Log.d(TAG, "Pet document not found")
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.e(TAG, "Error searching for pet document in Firestore", e)
            }
    }




    private fun displayImage(name: String?) {
        val iv_petProfile_image = findViewById<ImageView>(R.id.iv_petProfile_image)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val dbRef = FirebaseStorage.getInstance().reference.child("images_pet/$userId/$name.jpg")


        val localFile = File.createTempFile("tmpImage", "jpg")
        dbRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            iv_petProfile_image.setImageBitmap(bitmap)
        }
            .addOnFailureListener() {
                iv_petProfile_image.setImageResource(R.drawable.baseline_hide_image_24)
            }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
