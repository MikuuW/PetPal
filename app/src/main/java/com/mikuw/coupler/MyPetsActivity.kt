package com.mikuw.coupler

import ItemAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mikuw.coupler.data.Datasource

class MyPetsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pets)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "My Pets"

    }

}
