package com.mikuw.coupler

import Datasource_Firebase_Pets
import ShowPetsAdapter
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.mikuw.coupler.model.Pet
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import com.mikuw.coupler.model.Search
import com.squareup.picasso.Picasso

class SearchDetailsActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_details)
        val search = intent.getSerializableExtra("search") as? Search
        val title = search?.title
        val fromDate = search?.from
        val toDate = search?.to
        val creatorUid = search?.creator
        val desc = search?.desc

        // BURGER MENU / NAVIGATION DRAWER
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupNavigationDrawer(this)

        // assign views
        val tv_title = findViewById<TextView>(R.id.tv_search_details_title)
        val tv_date = findViewById<TextView>(R.id.tv_search_details_date)

        val et_desc = findViewById<TextView>(R.id.et_search_details_desc)


        // Count Days
        val days = if (fromDate != null && toDate != null) calculateDaysBetweenDates(
            fromDate,
            toDate
        ) else null
        // assign text
        tv_title.text = title
        tv_date.text = formattedDate(fromDate) + " - " + formattedDate(toDate) + " \n$days day(s)"
        et_desc.text = desc

        // get docId of the Search
        getCreatorImage(creatorUid)
        // TEST
        val recyclerView = findViewById<RecyclerView>(R.id.rv_search_details_pets)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val datasourceFirebasePets = Datasource_Firebase_Pets()
        //val docId = "C18FhpBNrI4qJ4vN1Ety"

        var docId = ""
        if (search != null) {
            getDocId(creatorUid, search.title) { searchId ->
                docId = searchId
                // Continue your code logic here using the obtained `doccId`
                datasourceFirebasePets.loadPetsinSearch(docId) { pets ->
                    val recyclerView = findViewById<RecyclerView>(R.id.rv_search_details_pets)
                    recyclerView.adapter = ShowPetsAdapter(this, pets).apply {
                        setOnItemClickListener(object : ShowPetsAdapter.OnItemClickListener {
                            override fun onItemClick(pet: Pet) {
                                val intent =
                                    Intent(
                                        this@SearchDetailsActivity,
                                        PetProfileShowActivity::class.java
                                    )
                                intent.putExtra("pet", pet)
                                startActivity(intent)
                            }
                        })
                    }

                    recyclerView.setHasFixedSize(true)
                }
            }
        }

        // handleClickOnPetsitter
        handleSearchCreator(search)
        // TEST ENDE
        handleUserNotLoggedIn()
    }

    private fun handleSearchCreator(search: Search?) {
        val tv_petsitter_name = findViewById<TextView>(R.id.iv_search_details_name)
        val tv_petsitter_location = findViewById<TextView>(R.id.iv_search_details_location)

        //Set views
        tv_petsitter_location.text = search?.location

        // Get the name of the Creator
        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection("users").document(search?.creator.toString())

        usersRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val firstname = documentSnapshot.getString("firstname")
                val lastname = documentSnapshot.getString("lastname")

                // Use the firstname and lastname variables as needed
                // For example, you can set the text of a TextView using these values
                tv_petsitter_name.text = "$firstname $lastname"
            }
        }.addOnFailureListener { exception ->
            // Handle any exceptions that occur
            Log.e(TAG, "Error getting user document", exception)
        }
    }


    private fun handleUserNotLoggedIn() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            val btn = findViewById<Button>(R.id.btn_search_details_button)
            btn.visibility = Button.GONE
        }
    }

    private fun getDocId(creatorUid: String?, title: String?, callback: (String) -> Unit) {
        if (creatorUid != null) {
            val db = FirebaseFirestore.getInstance()
            val searchesRef = db.collection("searches")
                .whereEqualTo("creator", creatorUid)
                .whereEqualTo("title", title)
                .limit(1)
            searchesRef.get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val searchId = document.id
                    callback(searchId) // Invoke the callback with the document ID
                } else {
                    Log.d(TAG, "No matching documents found")
                    callback("") // Invoke the callback with an empty string
                }
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: $exception")
                callback("") // Invoke the callback with an empty string
            }
        } else {
            callback("") // Invoke the callback with an empty string if the UID is null
        }
        handleButtonClick(creatorUid, title)
    }

    private fun handleButtonClick(creatorUid: String?, title: String?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val button = findViewById<Button>(R.id.btn_search_details_button)
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(creatorUid!!)
        if (creatorUid == userId) {
            button.text = "Mark as done"
            button.setOnClickListener {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to mark this search as done? This cannot be undone.")
                    .setPositiveButton("Confirm") { _, _ ->
                        // Perform the mark as done action
                        markSearchAsDone(creatorUid, title)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        } else {
            userRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val firstname = documentSnapshot.getString("firstname")
                    val lastname = documentSnapshot.getString("lastname")
                    button.text = "Contact $firstname $lastname"
                }

                button.setOnClickListener {
                    // ES gibt MessageWriteActivity und MessageAnswerActivity und MessageReadActivity
                    val intent = Intent(this, MessageWriteActivity::class.java)
                    intent.putExtra("receiverUid", creatorUid)
                    intent.putExtra("title", title)
                    startActivity(intent)
                }
            }
        }
    }

    private fun markSearchAsDone(creatorUid: String?, title: String?) {
        val db = FirebaseFirestore.getInstance()
        val searchesRef = db.collection("searches")
            .whereEqualTo("creator", creatorUid)
            .whereEqualTo("title", title)
            .limit(1)

        searchesRef.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents[0]
                val searchId = documentSnapshot.id

                // Update the document field
                db.collection("searches")
                    .document(searchId)
                    .update("isDone", true)
                    .addOnSuccessListener {
                        // Update successful
                        Log.d(TAG, "Search marked as done in Firestore")
                        val intent = Intent(this, SearchesListActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Log.e(TAG, "Error marking search as done in Firestore", e)
                    }
            } else {
                Log.d(TAG, "Search document not found")
            }
        }
            .addOnFailureListener { e ->
                // Handle failure
                Log.e(TAG, "Error searching for search document in Firestore", e)
            }
    }

    private fun getCreatorImage(creatorUid: String?) {
        println("CreatorUid: " + creatorUid)
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(creatorUid ?: "")

        userRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val image = documentSnapshot.getString("imageUri")
                if (image != null) {
                    val imageView = findViewById<ImageView>(R.id.iv_search_details_profile_image)
                    Picasso.get().load(image).resize(40, 40).centerCrop().into(imageView)
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun formattedDate(date: Date?): String {
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun calculateDaysBetweenDates(fromDate: Date, toDate: Date): Long {
        val diffInMillies = toDate.time - fromDate.time
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
    }
}

