package com.mikuw.coupler

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.data.Datasource_Animal_Types

class AddPetsActivity : AppCompatActivity() {

    private lateinit var selectedItem: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pets)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Add Pets"

        val tv_pet_name = findViewById<android.widget.EditText>(R.id.et_pet_name)
        val tv_pet_desc = findViewById<android.widget.EditText>(R.id.et_pet_desc)

        // Dropdown Menu
        val spinner_animal_types: Spinner = findViewById<Spinner>(R.id.spinner_animal_types)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Datasource_Animal_Types().options)
        spinner_animal_types.adapter = adapter

        spinner_animal_types.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedItem = Datasource_Animal_Types().options[position]
                // do something with selected item
                println(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do something when nothing is selected
            }
        }

        val btn_pet_submit = findViewById<View>(R.id.btn_pet_submit)
        btn_pet_submit.setOnClickListener {
            createPetInFirestore(tv_pet_name.text.toString(), tv_pet_desc.text.toString(), selectedItem)
            println(tv_pet_name.text.toString())
        }
    }

    private fun createPetInFirestore(name: String, desc: String, type: String) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Check if the name, desc, and type are not empty before creating the pet
        if (name.isNotEmpty() && desc.isNotEmpty() && type != "Select a type...") {
            checkIfPetAlreadyExists(name, userId) { petExists ->
                if (petExists) {
                    Toast.makeText(this, "Pet with name $name already exists", Toast.LENGTH_SHORT).show()
                } else {
                    val pet = hashMapOf(
                        "owner" to userId,
                        "name" to name,
                        "type" to type,
                        "desc" to desc
                    )

                    db.collection("pets")
                        .add(pet)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this, "$name saved", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MyPetsActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error adding document", e)
                        }
                }
            }
        } else {
            Toast.makeText(this, "Pet name, description, or type cannot be empty", Toast.LENGTH_SHORT).show()
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

}
