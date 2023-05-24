package com.mikuw.coupler

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
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
        iv_profile_edit_image.setOnClickListener {
            openImagePicker()
        }

        val btn_edit_submit = findViewById<TextView>(R.id.btn_edit_profile_submit)
        btn_edit_submit.setOnClickListener {
            updateUserOnSubmit()
            uploadToStorageAndUpdateFirestore()
        }

        val btn_edit_image = findViewById<TextView>(R.id.tv_edit_image_button)
        btn_edit_image.setOnClickListener {
            openImagePicker()
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


        var etv_edit_email = findViewById<TextView>(R.id.etv_edit_profile_email)
        var etv_edit_firstname = findViewById<TextView>(R.id.etv_edit_profile_firstname)
        var etv_edit_lastname = findViewById<TextView>(R.id.etv_edit_profile_lastname)
        var iv_profile_edit_image = findViewById<ImageView>(R.id.iv_profile_edit_image)
        var etv_edit_street = findViewById<TextView>(R.id.etv_edit_profile_street)
        var etv_edit_street_nr = findViewById<TextView>(R.id.etv_edit_profile_street_nr)
        var etv_edit_postalcode = findViewById<TextView>(R.id.etv_edit_profile_postalcode)
        var etv_edit_city = findViewById<TextView>(R.id.etv_edit_profile_city)


        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // retrieve the user's data
                val email = document.getString("email")
                val firstname = document.getString("firstname")
                val lastname = document.getString("lastname")
                val imageUrl = document.getString("imageUri")
                val street = document.getString("street")
                val street_nr = document.getString("streetNr")
                val postalcode = document.getString("postalcode")
                val city = document.getString("city")
                // do something with the retrieved data


                etv_edit_email.text = email
                etv_edit_firstname.text = firstname
                etv_edit_lastname.text = lastname
                etv_edit_street.text = street
                etv_edit_street_nr.text = street_nr
                etv_edit_postalcode.text = postalcode
                etv_edit_city.text = city

                Picasso.get()
                    .load(imageUrl)
                    .resize(200, 200)
                    .centerCrop()
                    .into(iv_profile_edit_image)

            } else {
                // handle the case when the document does not exist
                println("No such document")
            }
        }.addOnFailureListener { exception ->
            // handle any exceptions that occur
            println("Error getting documents: $exception")
        }


    }

    private fun updateUserOnSubmit() {
        val email = findViewById<TextView>(R.id.etv_edit_profile_email).text.toString()
        val firstname =
            findViewById<TextView>(R.id.etv_edit_profile_firstname).text.toString().trim()
        val lastname = findViewById<TextView>(R.id.etv_edit_profile_lastname).text.toString().trim()
        val street = findViewById<TextView>(R.id.etv_edit_profile_street).text.toString().trim()
        val streetNr =
            findViewById<TextView>(R.id.etv_edit_profile_street_nr).text.toString().trim()
        val postalcode =
            findViewById<TextView>(R.id.etv_edit_profile_postalcode).text.toString().trim()
        val city = findViewById<TextView>(R.id.etv_edit_profile_city).text.toString().trim()

        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

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
        if (email.isNotBlank()) {
            userUpdates["email"] = email
        }

        if (userUpdates.isNotEmpty()) {
            db.collection("users")
                .document(userId)
                .update(userUpdates)
                .addOnSuccessListener { documentReference ->
                    Log.d(ContentValues.TAG, "DocumentSnapshot updated with ID: $userId")
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error updating document", e)
                }
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
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                        takePictureIntent.resolveActivity(packageManager)?.also {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
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

    private fun uploadToStorageAndUpdateFirestore() {
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userUpdates = hashMapOf<String, Any>()

        val firstname =
            findViewById<TextView>(R.id.etv_edit_profile_firstname).text.toString().trim()
        val lastname = findViewById<TextView>(R.id.etv_edit_profile_lastname).text.toString().trim()

        uploadImageToFirebaseStorage(firstname, lastname) { uri ->
            if (uri != null) {
                userUpdates["imageUri"] = uri
                if (userUpdates.isNotEmpty()) {
                    db.collection("users")
                        .document(userId)
                        .update(userUpdates)
                        .addOnSuccessListener { documentReference ->
                            Log.d(ContentValues.TAG, "DocumentSnapshot updated with ID: $userId")
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error updating document", e)
                        }
                }
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

