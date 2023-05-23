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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mikuw.coupler.data.Datasource_Animal_Types

class PetAddActivity : AppCompatActivity() {

    private lateinit var selectedItem: String
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

        //TEST BURGER MENU
        imageUri = Uri.EMPTY
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        //TEST BURGER MENU

        val tv_pet_name = findViewById<android.widget.EditText>(R.id.et_pet_name)
        val tv_pet_desc = findViewById<android.widget.EditText>(R.id.et_pet_desc)

        // Dropdown Menu
        val spinner_animal_types: Spinner = findViewById<Spinner>(R.id.spinner_animal_types)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Datasource_Animal_Types().options
        )
        spinner_animal_types.adapter = adapter

        spinner_animal_types.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedItem = Datasource_Animal_Types().options[position]
                // do something with selected item
                println(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do something when nothing is selected    private var imageUri: Uri? = null // Declare imageUri as a member variable
            }
        }

        val btn_pet_submit = findViewById<View>(R.id.btn_pet_submit)
        btn_pet_submit.setOnClickListener {
            createPetInFirestore(
                tv_pet_name.text.toString(),
                tv_pet_desc.text.toString(),
                selectedItem,
                imageUri
            )


        }

        // TEST IMAGE UPLOAD
        iv_pet_add_image = findViewById(R.id.iv_pet_add_image)
        iv_pet_add_image.setOnClickListener {
            openImagePicker()
        }


        // TEST IMAGE UPLOAD ENDE
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
            iv_pet_add_image.setImageBitmap(imageBitmap as Bitmap)
        }
    }

    private fun uploadImageToFirebaseStorage(name: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("images_pet/$userId/")
        val imageName = "$name.jpg"
        val imageRef = storageRef.child(imageName)

        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            // Image upload successful

            // Get the URL of the uploaded image
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()

                // Do something with the image URL, such as storing it in Firestore
            }
        }.addOnFailureListener { exception ->
            // Image upload failed
        }
    }


    private fun createPetInFirestore(name: String, desc: String, type: String, imageUri: Uri?) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Check if the name, desc, and type are not empty before creating the pet
        if (name.isNotEmpty() && desc.isNotEmpty() && type != "Select a type...") {
            checkIfPetAlreadyExists(name, userId) { petExists ->
                if (petExists) {
                    Toast.makeText(this, "Pet with name $name already exists", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val pet = hashMapOf(
                        "owner" to userId,
                        "name" to name,
                        "type" to type,
                        "desc" to desc,
                        "imageUri" to imageUri
                    )
                    uploadImageToFirebaseStorage(name)
                    db.collection("pets")
                        .add(pet)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "$name saved", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, PetsShowActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error adding document", e)
                        }
                }
            }
        } else {

            val intent = Intent(this, PetAddActivity::class.java)
            startActivity(intent)
            Toast.makeText(
                this,
                "Pet name, description, or type cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

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
