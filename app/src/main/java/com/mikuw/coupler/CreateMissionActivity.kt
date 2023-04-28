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
            println("Number of days between $fromDate and $toDate: $days")
        }

        // Initialize data.
        val datasourceFirebasePets = Datasource_Firebase_Pets()
        val recyclerView = findViewById<RecyclerView>(R.id.rv_select_pets)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        datasourceFirebasePets.loadPets { pets ->
            recyclerView.adapter = SelectPetsAdapter(this, pets)
            recyclerView.setHasFixedSize(true)
        }
        //TODO:
        //Ausgewählte Tiere der Funktion geben
        //Funktion erstellen, die die Tiere in die Datenbank schreibt

        val btn_submit = findViewById<Button>(R.id.btn_create_mission)


        btn_submit.setOnClickListener {
            //createMissionInFirestore(fromDate, toDate)
            val intent = Intent(this, tmpActivity::class.java)
            startActivity(intent)
        }


    }

    private fun createMissionInFirestore(fromDate: Date, toDate: Date) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Check if the name, desc, and type are not empty before creating the pet
        if (true) {
            val pet = hashMapOf(
                "from" to fromDate,
                "to" to toDate,
                //"pet" to pet,
            )
            db.collection("searches")
                .add(pet)
                .addOnSuccessListener { documentReference ->
                    //Toast.makeText(this, "Mission for $name created", Toast.LENGTH_SHORT).show()
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
                            tvAmountDays.text = "$weeks Weeks"
                        } else {
                            tvAmountDays.text = "$days Days"
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
