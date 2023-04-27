package com.mikuw.coupler

import EventsAdapter
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mikuw.coupler.data.Datasource_Firebase_Events
import com.mikuw.coupler.model.Pet
import java.io.File

class PetProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_profile)


        //TODO:
        // Probleme mit Berechtigungen...
        val pet = intent.getSerializableExtra("pet") as? Pet
        val name = pet?.name
        val desc = pet?.desc
        val imageUri = pet?.imageUri
        println(imageUri)




        // Assuming you have the imageUri string in a variable called imageUri
        val uri = Uri.parse(imageUri)
        val iv_petProfile_image = findViewById<ImageView>(R.id.iv_petProfile_image)
        //iv_petProfile_image.setImageURI(uri)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "$name's Profile"

        val tv_pet_desc = findViewById<TextView>(R.id.et_pet_desc)
        tv_pet_desc.text = desc

        //TEST
        val imageName = "1d7f52b2-138e-4b0d-8f78-ac52d0a60d2a.jpg"
        val testUri = "content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F25/ORIGINAL/NONE/1884294261"
        val testRes = FirebaseStorage.getInstance().reference.child("images_pet/$imageName")

        val localFile = File.createTempFile("tmpImage", "jpg")
        testRes.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            iv_petProfile_image.setImageBitmap(bitmap)
        }
        //TEST
    }


}
