package com.mikuw.coupler

import Datasource_Firebase_Pets
import SelectPetsAdapter
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikuw.coupler.model.Pet
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CreateMissionActivity : AppCompatActivity() {

    private lateinit var fromDate: Date
    private lateinit var toDate: Date


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_mission)

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
}
