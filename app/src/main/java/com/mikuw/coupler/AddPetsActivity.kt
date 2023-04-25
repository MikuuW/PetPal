package com.mikuw.coupler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AddPetsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_pets)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Add Pets"

    }

}
