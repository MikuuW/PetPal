package com.mikuw.coupler

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
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
import com.squareup.picasso.Picasso

class UserProfileEditActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private lateinit var iv_profile_edit_image: ImageView
    private lateinit var imageUri: Uri
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_edit)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Edit Profile"

        //TEST BURGER MENU
        imageUri = Uri.EMPTY
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        //TEST BURGER MENU

        getProfileInformation()

        iv_profile_edit_image = findViewById(R.id.iv_profile_edit_image)
        val btn_edit_image = findViewById<TextView>(R.id.tv_edit_image_button)

        iv_profile_edit_image.setOnClickListener {
            openImagePicker()
        }
        btn_edit_image.setOnClickListener {
            openImagePicker()
        }


        val btn_edit_submit = findViewById<TextView>(R.id.btn_edit_profile_submit)
        btn_edit_submit.setOnClickListener {
            updateUserOnSubmit { success ->
                if (success) {
                    uploadToStorageAndUpdateFirestore { uri ->
                        if (uri != null) {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT)
                                .show()
                            // Upload and update completed successfully
                            val intent = Intent(this, UserProfileShowActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Error occurred during upload and update
                            Toast.makeText(
                                this,
                                "Error occurred during upload and update",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    // Error occurred during user update
                    Toast.makeText(this, "Error occurred during user update", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun getProfileInformation() {

        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.d(ContentValues.TAG, "No user is logged in")
            return
        }
        val userRef = db.collection("users").document(currentUser.uid)


        var etv_edit_firstname = findViewById<TextView>(R.id.etv_edit_profile_firstname)
        var etv_edit_lastname = findViewById<TextView>(R.id.etv_edit_profile_lastname)
        var iv_profile_edit_image = findViewById<ImageView>(R.id.iv_profile_edit_image)
        var etv_edit_street = findViewById<TextView>(R.id.etv_edit_profile_street)
        var etv_edit_street_nr = findViewById<TextView>(R.id.etv_edit_profile_street_nr)
        var etv_edit_postalcode = findViewById<TextView>(R.id.etv_edit_profile_postalcode)
        var etv_edit_city = findViewById<TextView>(R.id.etv_edit_profile_city)
        var etv_edit_desc = findViewById<TextView>(R.id.tv_profile_edit_desc)


        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // Werte holen
                val firstname = document.getString("firstname")
                val lastname = document.getString("lastname")
                val imageUrl = document.getString("imageUri")
                val street = document.getString("street")
                val street_nr = document.getString("streetNr")
                val postalcode = document.getString("postalcode")
                val city = document.getString("city")
                val desc = document.getString("desc")

                // Werte setzen
                etv_edit_firstname.text = firstname
                etv_edit_lastname.text = lastname
                etv_edit_street.text = street
                etv_edit_street_nr.text = street_nr
                etv_edit_postalcode.text = postalcode
                etv_edit_city.text = city
                etv_edit_desc.text = desc

                // Bild setzen wenn vorhanden
                if (!imageUrl.isNullOrEmpty()) {
                    Picasso.get()
                        .load(imageUrl)
                        .resize(200, 200)
                        .centerCrop()
                        .into(iv_profile_edit_image)
                }
            } else {
                // handle the case when the document does not exist
                println("No such document")
            }
        }.addOnFailureListener { exception ->
            // handle any exceptions that occur
            println("Error getting documents: $exception")
        }
    }

    private fun updateUserOnSubmit(callback: (Boolean) -> Unit) {
        // Werte holen
        val firstname =
            findViewById<TextView>(R.id.etv_edit_profile_firstname).text.toString().trim()
        val lastname = findViewById<TextView>(R.id.etv_edit_profile_lastname).text.toString().trim()
        val street = findViewById<TextView>(R.id.etv_edit_profile_street).text.toString().trim()
        val streetNr =
            findViewById<TextView>(R.id.etv_edit_profile_street_nr).text.toString().trim()
        val postalcode =
            findViewById<TextView>(R.id.etv_edit_profile_postalcode).text.toString().trim()
        val city = findViewById<TextView>(R.id.etv_edit_profile_city).text.toString().trim()
        val desc = findViewById<TextView>(R.id.tv_profile_edit_desc).text.toString().trim()

        // Instanziieren
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Werte setzen
        val userUpdates = hashMapOf<String, Any>()

        if (firstname.isNotBlank()) {
            userUpdates["firstname"] = firstname
        }
        if (lastname.isNotBlank()) {
            userUpdates["lastname"] = lastname
        }
        if (street.isNotBlank()) {
            userUpdates["street"] = street
        }
        if (streetNr.isNotBlank()) {
            userUpdates["streetNr"] = streetNr
        }
        if (postalcode.isNotBlank()) {
            userUpdates["postalcode"] = postalcode
        }
        if (city.isNotBlank()) {
            userUpdates["city"] = city
        }
        if (desc.isNotBlank()) {
            userUpdates["desc"] = desc
        }

        // Update User in Firestore
        if (userUpdates.isNotEmpty()) {
            db.collection("users")
                .document(userId)
                .update(userUpdates)
                .addOnSuccessListener { documentReference ->
                    updatePetsitter(userId, userUpdates)
                    Log.d(ContentValues.TAG, "DocumentSnapshot updated with ID: $userId")
                    callback(true) // User update success
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show()
                    Log.w(ContentValues.TAG, "Error updating document", e)
                    callback(false) // User update failed
                }
        } else {
            callback(true) // No updates needed, consider it a success
        }
    }

    private fun updatePetsitter(userId: String, userUpdates: HashMap<String, Any>) {
        val db = FirebaseFirestore.getInstance()
        checkIfPetsitter(userId) { isPetsitter ->
            if (isPetsitter) {
                // User is a petsitter
                if (userUpdates.isNotEmpty()) {
                    db.collection("petsitters")
                        .document(userId)
                        .update(userUpdates)
                }
            }
        }
    }

    private fun checkIfPetsitter(userId: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val isPetsitter = documentSnapshot.getBoolean("isPetsitter") ?: false
                callback(isPetsitter)
            }
            .addOnFailureListener { e ->
                // Error occurred
                println("Error checking isPetsitter field for user with ID: $userId")
                e.printStackTrace()
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
                    updateImageUriInFirestore("users", "") // Update Firestore with an empty string
                    updateImageUriInFirestore(
                        "petsitters",
                        ""
                    ) // Update Firestore with an empty string
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }


    private fun updateImageUriInFirestore(collection: String, imageUri: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val userUpdates = hashMapOf<String, Any>("imageUri" to imageUri)

        db.collection(collection).document(userId)
            .update(userUpdates)
            .addOnSuccessListener {
                Log.d(TAG, "ImageUri field updated with an empty string.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating ImageUri field", e)
            }
    }

    private fun setImageFromPicker(data: Intent?) {
        data?.data?.let { imageUri ->
            iv_profile_edit_image.setImageURI(imageUri)
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
            iv_profile_edit_image.setImageBitmap(imageBitmap as Bitmap)
        }
    }

    private fun uploadToStorageAndUpdateFirestore(callback: (Uri?) -> Unit) {
        val firstname =
            findViewById<TextView>(R.id.etv_edit_profile_firstname).text.toString().trim()
        val lastname = findViewById<TextView>(R.id.etv_edit_profile_lastname).text.toString().trim()

        uploadImageToFirebaseStorage(firstname, lastname) { uri ->
            if (uri != null && uri.toString().isNotEmpty() && uri.toString().isNotBlank()) {
                updateImageUriInFirestore("users", uri.toString())
                updateImageUriInFirestore("petsitters", uri.toString())
                callback(uri)
            } else {
                callback(null) // Error occurred during image upload
            }
        }
    }


    private fun uploadImageToFirebaseStorage(
        firstname: String,
        lastname: String,
        callback: (Uri?) -> Unit
    ) {
        val timestamp = System.currentTimeMillis()
        val storageRef = FirebaseStorage.getInstance().reference.child("images_user/")
        val imageName = "$firstname-$lastname-$timestamp.jpg"
        val imageRef = storageRef.child(imageName)

        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                callback(uri)
            }
        }.addOnFailureListener { exception ->
            callback(null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

