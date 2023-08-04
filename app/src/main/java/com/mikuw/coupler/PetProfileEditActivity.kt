package com.mikuw.coupler

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mikuw.coupler.model.Pet
import com.squareup.picasso.Picasso
import java.io.File

class PetProfileEditActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private lateinit var iv_petProfile_image: ImageView
    private lateinit var imageUri: Uri
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_profile_edit)

        // BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)
        val navView: NavigationView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupNavigationDrawer(this)
        imageUri = Uri.EMPTY
        val pet = intent.getSerializableExtra("pet") as? Pet
        val name = pet?.name
        val desc = pet?.desc
        val imageUri = pet?.imageUrl

        // Actionbar
        val actionBar = supportActionBar
        actionBar?.title = "$name's Profile"

        val tv_pet_desc = findViewById<TextView>(R.id.et_pet_desc)
        tv_pet_desc.text = desc

        // Display Image
        displayImage(imageUri)

        iv_petProfile_image = findViewById(R.id.iv_petProfile_image)
        val btn_edit_image = findViewById<TextView>(R.id.tv_PetProfile_edit_image_button)

        iv_petProfile_image.setOnClickListener {
            openImagePicker()
        }
        btn_edit_image.setOnClickListener {
            openImagePicker()
        }

        val btn = findViewById<Button>(R.id.btn_pet_submit)

        btn.setOnClickListener() {
            val newDesc = tv_pet_desc.text.toString()

            updatePetInFirestore(name, newDesc) { updatePetSuccess ->
                if (updatePetSuccess) {
                    // Pet update was successful
                    Log.d(TAG, "Pet updated in Firestore")

                    uploadImageToFirebaseStorage(name) { uploadImageSuccess ->
                        if (uploadImageSuccess) {
                            // Image upload was successful
                            Log.d(TAG, "Image uploaded to Firebase Storage")
                            Toast.makeText(this, "Pet profile updated", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@PetProfileEditActivity, PetsListActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Image upload failed
                            Log.e(TAG, "Error uploading image to Firebase Storage")
                            // Handle the error or show an error message
                        }
                    }
                } else {
                    // Pet update failed
                    Log.e(TAG, "Error updating pet in Firestore")
                    // Handle the error or show an error message
                }
            }
        }

    }


    private fun uploadImageToFirebaseStorage(name: String?, callback: (Boolean) -> Unit) {
        val timestamp = System.currentTimeMillis()
        val storageRef = FirebaseStorage.getInstance().reference.child("images_pets/")
        val imageName = "$name-$timestamp.jpg"
        val imageRef = storageRef.child(imageName)

        if (imageUri != Uri.EMPTY) {
            val uploadTask = imageRef.putFile(imageUri)
            uploadTask.addOnSuccessListener { uploadTaskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    updateUriInFirestore(name, uri.toString()) { uriUpdateSuccess ->
                        if (uriUpdateSuccess) {
                            // Image URI update completed successfully
                            Log.d(TAG, "Image URI updated in Firestore")
                            callback(true)
                        } else {
                            // Image URI update failed
                            Log.e(TAG, "Error updating image URI in Firestore")
                            callback(false)
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                // Image upload failed
                Log.e(TAG, "Error uploading image to Firebase Storage")
                callback(false)
            }
        } else {
            // Image URI is empty
            callback(true) // No image to upload, consider it as success
        }
    }


    private fun updatePetInFirestore(name: String?, desc: String?, callback: (Boolean) -> Unit) {
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
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            // Handle failure
                            Log.e(TAG, "Error updating pet in Firestore", e)
                            callback(false)
                        }
                } else {
                    Log.d(TAG, "Pet document not found")
                    callback(false)
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.e(TAG, "Error searching for pet document in Firestore", e)
                callback(false)
            }
    }


    fun openImagePicker() {
        val REQUEST_IMAGE_CAPTURE = 1
        val REQUEST_PICK_IMAGE = 2

        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Delete", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                        takePictureIntent.resolveActivity(packageManager)?.also {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                }
                options[item] == "Choose from Gallery" -> {
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    ).also { pickPhotoIntent ->
                        startActivityForResult(pickPhotoIntent, REQUEST_PICK_IMAGE)
                    }
                }
                options[item] == "Delete" -> {
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun setImageFromPicker(data: Intent?) {
        data?.data?.let { imageUri ->
            iv_petProfile_image.setImageURI(imageUri)
            this.imageUri = imageUri
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    setImageFromCamera(data)
                }
                REQUEST_PICK_IMAGE -> {
                    setImageFromPicker(data)
                }
            }
        }
    }

    private fun setImageFromCamera(data: Intent?) {
        data?.extras?.get("data")?.let { imageBitmap ->
            iv_petProfile_image.setImageBitmap(imageBitmap as Bitmap)
        }
    }



    private fun updateUriInFirestore(name: String?, uri: String?, callback: (Boolean) -> Unit) {
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
                        .update("imageUri", uri)
                        .addOnSuccessListener {
                            // Update successful
                            callback(true)
                        }
                        .addOnFailureListener { e ->
                            // Handle failure
                            callback(false)
                        }
                } else {
                    Log.d(TAG, "Pet document not found")
                    callback(false)
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                callback(false)
            }
    }


    private fun displayImage(uri: String?) {
        val iv_petProfile_image = findViewById<ImageView>(R.id.iv_petProfile_image)
        Picasso.get()
            .load(uri)
            .resize(200, 200)
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
