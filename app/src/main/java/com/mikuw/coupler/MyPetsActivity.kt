package com.mikuw.coupler

import PetsAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mikuw.coupler.data.Datasource_Firebase_Pets

class MyPetsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pets)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "My Pets"

        // Initialize data.
        val datasourceFirebaseEvents = Datasource_Firebase_Pets()

        datasourceFirebaseEvents.loadPets { pets ->
            val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
            recyclerView.adapter = PetsAdapter(this, pets)

            // Use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true)
        }

    }

}
