package com.mikuw.coupler

import Datasource_Firebase_Pets
import SelectPetsAdapter
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Pet
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CreateMissionActivity : AppCompatActivity() {

    private lateinit var fromDate: Date
    private lateinit var toDate: Date
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_mission)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Create new Search for your Pet"

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        //TEST BURGER MENU

        val fromDatePickerButton = findViewById<Button>(R.id.btn_select_from)
        fromDatePickerButton.setOnClickListener {
            showDatePicker(fromDatePickerButton, isFromDate = true)
        }

        val toDatePickerButton = findViewById<Button>(R.id.btn_select_to)
        toDatePickerButton.setOnClickListener {
            showDatePicker(toDatePickerButton, isFromDate = false)
        }

        val tv_amount_days = findViewById<TextView>(R.id.tv_amount_days)
        tv_amount_days.setOnClickListener {
            val days = calculateDaysBetweenDates(fromDate, toDate)
        }

        val datasourceFirebasePets = Datasource_Firebase_Pets()
        val recyclerView = findViewById<RecyclerView>(R.id.rv_select_pets)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        datasourceFirebasePets.loadPets { pets ->
            val adapter = SelectPetsAdapter(this, pets)
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)

            val btn_submit = findViewById<Button>(R.id.btn_create_mission)
            val desc = findViewById<EditText>(R.id.et_search_desc)
            val title = findViewById<EditText>(R.id.et_search_title)



            btn_submit.setOnClickListener {
                val selectedPets = adapter.getSelectedPets()
                createMissionInFirestore(
                    title.text.toString(),
                    fromDate,
                    toDate,
                    desc.text.toString(),
                    selectedPets
                )
            }
        }
    }

    private fun createMissionInFirestore(
        title: String,
        fromDate: Date,
        toDate: Date,
        desc: String,
        pets: List<Pet>
    ) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Check if the name, desc, and type are not empty before creating the pet
        if (true) {
            val pet = hashMapOf(
                "title" to title,
                "creator" to userId,
                "from" to fromDate,
                "to" to toDate,
                "desc" to desc,
                "pets" to pets
            )
            db.collection("searches")
                .add(pet)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Search created", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding document", e)
                }
        } else {
            Toast.makeText(
                this,
                "Pet name, description, or type cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDatePicker(selectButton: Button, isFromDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                if (isFromDate) {
                    fromDate = selectedDate.time
                } else {
                    toDate = selectedDate.time
                    if (fromDate != null) {
                        val days = calculateDaysBetweenDates(fromDate, toDate)
                        var amount_days = days
                        val tvAmountDays = findViewById<TextView>(R.id.tv_amount_days)
                        var weeks: Long? = null
                        if (days % 7 == 0L) {
                            weeks = amount_days / 7
                            tvAmountDays.text = "$weeks Week(s)"
                        } else {
                            tvAmountDays.text = "$days Day(s)"
                        }
                    }
                }
                selectButton.text =
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(selectedDate.time)
            }, year, month, dayOfMonth
        )

        datePickerDialog.setOnCancelListener {
            selectButton.text = "pick a date"
        }

        datePickerDialog.show()
    }


    private fun calculateDaysBetweenDates(fromDate: Date, toDate: Date): Long {
        val diffInMillies = toDate.time - fromDate.time
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
