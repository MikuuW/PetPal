package com.mikuw.coupler

import EventsAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mikuw.coupler.data.Datasource_Firebase_Events

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Main"

        // Initialize data.
        val datasourceFirebaseEvents = Datasource_Firebase_Events()

        datasourceFirebaseEvents.loadEvents { events ->
            val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
            recyclerView.adapter = EventsAdapter(this, events)

            // Use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true)
        }
    }

}
