package com.mikuw.coupler

import com.mikuw.coupler.data.Datasource_Firebase_Searches
import com.mikuw.coupler.adapter.SearchesAdapter
import com.mikuw.coupler.adapter.PetsitterAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.mikuw.coupler.data.Datasource_Firebase_Petsitter
import com.mikuw.coupler.model.Petsitter
import com.mikuw.coupler.model.Search

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // BURGER MENU / NAVIGATION DRAWER
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Main"


        //SWITCH
        loadSearches()
        val switch = findViewById<SwitchMaterial>(R.id.switch_petsitter)
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Load petsitter in Recyclerview
                loadPetsitters()
                val recyclerView = findViewById<RecyclerView>(R.id.rv_show_pets)
                recyclerView.adapter = null
            } else {
                // Load searches in RecyclerView
                loadSearches()
            }
        }


    }

    // Function that loads all petsitters
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

    // Function that loads all searches
    private fun loadSearches() {
        val datasourceFirebaseSearches = Datasource_Firebase_Searches()

        val recyclerView = findViewById<RecyclerView>(R.id.rv_show_pets)
        val searchesAdapter = SearchesAdapter(this, emptyList())
        recyclerView.adapter = searchesAdapter

        datasourceFirebaseSearches.loadSearches { searches ->
            val sortedSearches = searches.sortedBy { it.from }
            searchesAdapter.dataset = sortedSearches
            searchesAdapter.notifyDataSetChanged()
        }

        searchesAdapter.setOnItemClickListener(object : SearchesAdapter.OnItemClickListener {
            override fun onItemClick(search: Search) {
                val intent = Intent(this@MainActivity, SearchDetailsActivity::class.java)
                intent.putExtra("search", search)
                startActivity(intent)
            }
        })
        recyclerView.setHasFixedSize(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
