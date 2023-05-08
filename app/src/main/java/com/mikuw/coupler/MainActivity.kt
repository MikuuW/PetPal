package com.mikuw.coupler

import EventsAdapter
import PetsitterAdapter
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.mikuw.coupler.data.Datasource_Firebase_Events
import com.mikuw.coupler.data.Datasource_Firebase_Petsitter


//TODO:
// - Bewertungen für Tiere und Petsitter
// - Nachrichten schicken / Chat
// - Searches clickbar mit mehr Infos
// - Petsitter clickbar mi mehr Infos
// - Überlegen ob Basis Account und dann ggf.
//   die Möglichkeit ein Petsitter zu werden

// Optional:
// - Profilbild wenn BurgerMenu aufklappt
class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        //TEST BURGER MENU

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Main"




        //SWITCH
        loadSearches()
        val switch = findViewById<SwitchMaterial>(R.id.switch_petsitter)
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Load events from the second collection
                loadPetsitters()
                println("HEllo")
                val recyclerView = findViewById<RecyclerView>(R.id.rv_show_pets)
                recyclerView.adapter = null
                //loadOriginalCollectionEvents()
            } else {
                // Load events from the original collection
                loadSearches()
            }
        }


        //SWITCH




    }
    private fun loadPetsitters() {
        // Initialize data.
        val datasourceFirebasePetsitters = Datasource_Firebase_Petsitter()

        datasourceFirebasePetsitters.loadPetsitter { petsitter ->
            val recyclerView = findViewById<RecyclerView>(R.id.rv_show_pets)
            recyclerView.adapter = PetsitterAdapter(this, petsitter)

            // Use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true)
        }
    }
    private fun loadSearches() {
        // Initialize data.
        val datasourceFirebaseEvents = Datasource_Firebase_Events()

        datasourceFirebaseEvents.loadEvents { events ->
            val recyclerView = findViewById<RecyclerView>(R.id.rv_show_pets)
            recyclerView.adapter = EventsAdapter(this, events)

            // Use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // SWITCH

}
