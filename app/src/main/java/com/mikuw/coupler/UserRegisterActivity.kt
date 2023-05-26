package com.mikuw.coupler

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserRegisterActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private lateinit var iv_register_image: ImageView
    private lateinit var imageUri: Uri
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Register"

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

        //TEST IMAGE PICKER
        iv_register_image = findViewById<ImageView>(R.id.iv_register_image)
        iv_register_image.setOnClickListener {
            openImagePicker()
        }

        // Variablen zuweisen

        val registerButton = findViewById<Button>(R.id.button_register)
        registerButton.setOnClickListener {
            registerUser(

            )
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(

    ) {
        val email = findViewById<TextView>(R.id.etv_register_email).text.toString()
        val firstname = findViewById<TextView>(R.id.etv_register_firstname).text.toString()
        val lastname = findViewById<TextView>(R.id.etv_register_lastname).text.toString()
        val street = findViewById<TextView>(R.id.etv_register_street).text.toString()
        val streetNr = findViewById<TextView>(R.id.etv_register_street_nr).text.toString()
        val postalcode = findViewById<TextView>(R.id.etv_register_postalcode).text.toString()
        val city = findViewById<TextView>(R.id.etv_register_city).text.toString()
        val password = findViewById<TextView>(R.id.etv_register_password).text.toString()
        val passwordConfirmation =
            findViewById<TextView>(R.id.etv_register_passwordConfirmation).text.toString()

        val auth = FirebaseAuth.getInstance()

        // Wenn ein Feld leer ist oder Passwörter nicht übereinstimmen
        if (email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields.", Toast.LENGTH_SHORT).show()
        } else if (password != passwordConfirmation) {
            Toast.makeText(this, "Passwords does not match!", Toast.LENGTH_SHORT).show()
        }

        // Get reference to the AGB checkbox
        val agbCheckBox = findViewById<CheckBox>(R.id.cb_agb)

        // Check if AGB checkbox is checked
        if (!agbCheckBox.isChecked) {
            Toast.makeText(this, "You must accept our AGBs", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login successful, you can get the user information from the AuthResult object
                    var userId = FirebaseAuth.getInstance().currentUser?.uid
                    createUserInFirestore(
                        firstname,
                        lastname,
                        street,
                        streetNr,
                        postalcode,
                        city,
                        email,
                        userId ?: ""
                    )
                    val user = task.result?.user
                    Log.d(TAG, "User account created with email: ${user?.email}")
                    Toast.makeText(
                        this, "User created successful", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Login failed, handle specific types of exceptions
                    when (val exception = task.exception) {
                        is FirebaseAuthWeakPasswordException -> {
                            Toast.makeText(
                                this, "The password is too weak.", Toast.LENGTH_SHORT
                            ).show()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(
                                this, "The email address is not valid.", Toast.LENGTH_SHORT
                            ).show()
                        }
                        is FirebaseAuthUserCollisionException -> {
                            Toast.makeText(
                                this, "The email address is already in use.", Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            // Unknown error occurred, display a generic error message
                            Toast.makeText(
                                this, "An error occurred: ${exception?.message}", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user account", e)
            Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createUserInFirestore(
        firstname: String,
        lastname: String,
        street: String?,
        streetNr: String?,
        postalcode: String?,
        city: String?,
        email: String,
        userId: String
    ) {
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()

        // Create a new user object with the given email
        uploadImageToFirebaseStorage(firstname, lastname) { imageUri ->
            val user = hashMapOf(
                "imageUri" to imageUri,
                "firstname" to firstname,
                "lastname" to lastname,
                "street" to street,
                "streetNr" to streetNr,
                "postalcode" to postalcode,
                "city" to city,
                "email" to email,
                "isPetsitter" to false
            )
            db.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: $userId")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }

        // Set the user object to a new document in the "users" collection with the user ID as the document ID

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    fun openImagePicker() {
        val REQUEST_IMAGE_CAPTURE = 1
        val REQUEST_PICK_IMAGE = 2

        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
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
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun setImageFromPicker(data: Intent?) {
        data?.data?.let { imageUri ->
            iv_register_image.setImageURI(imageUri)
            this.imageUri = imageUri
            println("In SetImageFromPicker: $imageUri")
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
            iv_register_image.setImageBitmap(imageBitmap as Bitmap)
        }
    }

    private fun uploadImageToFirebaseStorage(
        firstname: String,
        lastname: String,
        callback: (Uri?) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("images_user/")
        val imageName = "$firstname-$lastname.jpg"
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
} //end
