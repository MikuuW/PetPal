package com.mikuw.coupler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mikuw.coupler.R

class CreateMissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_mission)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Create Mission"
    }
}