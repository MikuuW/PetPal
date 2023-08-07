package com.mikuw.coupler

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class PetAddActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private lateinit var iv_pet_add_image: ImageView
    private lateinit var imageUri: Uri
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets_add)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Add Pets"

        // BURGER MENU / NAVIGATION DRAWER
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)

        imageUri = Uri.EMPTY
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)

        val tv_pet_name = findViewById<android.widget.EditText>(R.id.et_pet_name)
        val tv_pet_desc = findViewById<android.widget.EditText>(R.id.et_pet_desc)

        val btn_pet_submit = findViewById<View>(R.id.btn_pet_submit)
        btn_pet_submit.setOnClickListener {
            createPetInFirestore(
                tv_pet_name.text.toString(),
                tv_pet_desc.text.toString(),
            )
        }
        iv_pet_add_image = findViewById(R.id.iv_pet_add_image)
        iv_pet_add_image.setOnClickListener {
            openImagePicker()
        }
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
            iv_pet_add_image.setImageURI(imageUri)
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
            iv_pet_add_image.setImageBitmap(imageBitmap as Bitmap)
        }
    }

    // upload image to firebase storage
    private fun uploadImageToFirebaseStorage(name: String, callback: (Uri?) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("images_pet/$userId/")
        val imageName = "$name.jpg"
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

    // create new pet in database and check if its already exists
    private fun createPetInFirestore(name: String, desc: String) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        // Check if the name or desc are not empty before creating the pet
        if (name.isNotEmpty() && desc.isNotEmpty()) {
            checkIfPetAlreadyExists(name, userId) { petExists ->
                if (petExists) {
                    Toast.makeText(this, "Pet with name $name already exists", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    uploadImageToFirebaseStorage(name) { imageUri ->
                        val pet = hashMapOf(
                            "owner" to userId,
                            "name" to name,
                            "desc" to desc,
                            "imageUri" to imageUri
                        )
                        db.collection("pets")
                            .add(pet)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(this, "$name saved", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, PetsListActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error adding document", e)
                            }
                    }
                }
            }
        } else {
            Toast.makeText(
                this,
                "Pet name or description cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // function that checks if pet is already in the database
    private fun checkIfPetAlreadyExists(name: String, userId: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("pets")
            .whereEqualTo("owner", userId)
            .whereEqualTo("name", name)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(false)
                } else {
                    callback(true)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                callback(false)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
