package com.mikuw.coupler

import Datasource_Firebase_Searches
import SearchesAdapter
import PetsitterAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.mikuw.coupler.data.Datasource_Firebase_Petsitter
import com.mikuw.coupler.model.Petsitter
import com.mikuw.coupler.model.Search




class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)
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
                val recyclerView = findViewById<RecyclerView>(R.id.rv_show_pets)
                recyclerView.adapter = null
                //loadOriginalCollectionEvents()
            } else {
                // Load events from the original collection
                loadSearches()
            }
        }


    }

    private fun loadPetsitters() {
        val datasourceFirebasePetsitter = Datasource_Firebase_Petsitter()

        val recyclerView = findViewById<RecyclerView>(R.id.rv_show_pets)
        val petsitterAdapter = PetsitterAdapter(this, emptyList())

        recyclerView.adapter = petsitterAdapter

        datasourceFirebasePetsitter.loadPetsitter { petsitters ->
            val recyclerView = findViewById<RecyclerView>(R.id.rv_show_pets)
            recyclerView.adapter = PetsitterAdapter(this, petsitters).apply {
                setOnItemClickListener(object : PetsitterAdapter.OnItemClickListener {
                    override fun onItemClick(petsitter: Petsitter) {
                        val intent = Intent(this@MainActivity, PetsitterDetailsActivity::class.java)
                        intent.putExtra("petsitter", petsitter)
                        startActivity(intent)
                    }
                })
            }
            recyclerView.setHasFixedSize(true)
        }


    }

    private fun loadSearches() {
        // Initialize data.
        val datasourceFirebaseSearches = Datasource_Firebase_Searches()

        val recyclerView = findViewById<RecyclerView>(R.id.rv_show_pets)
        val searchesAdapter = SearchesAdapter(this, emptyList())
        recyclerView.adapter = searchesAdapter

        // Load the searches from the data source
        datasourceFirebaseSearches.loadSearches { searches ->
            val sortedSearches = searches.sortedBy { it.from }
            searchesAdapter.dataset = sortedSearches
            searchesAdapter.notifyDataSetChanged()
        }

        // Set the item click listener for the searches adapter
        searchesAdapter.setOnItemClickListener(object : SearchesAdapter.OnItemClickListener {
            override fun onItemClick(search: Search) {
                val intent = Intent(this@MainActivity, SearchDetailsActivity::class.java)
                intent.putExtra("search", search)
                startActivity(intent)
            }
        })

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}
