package com.mikuw.coupler

import Datasource_Firebase_Searches
import SearchesAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.mikuw.coupler.model.Search

class SearchesListActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searches_list)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "My Searches"

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        // WEITER

        // Daten laden
        loadMySearches()
        loadMyPastSearches()

        // Views zuweisen
        setView(
            findViewById(R.id.my_searches_list),
            findViewById(R.id.my_searches_row_header),
            findViewById(R.id.my_searches_icon)
        )
        setView(
            findViewById(R.id.past_searches_list),
            findViewById(R.id.past_searches_row_header),
            findViewById(R.id.past_searches_icon)
        )
        // TODO:
        //  - My Searches
        //  - Past Searches
        //  - Solved Pet Sittings
    }

    private fun setView(recyclerView: RecyclerView, title: LinearLayout, icon: ImageView) {
        recyclerView.visibility = RecyclerView.GONE
        icon.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)

        title.setOnClickListener {
            if (recyclerView.visibility == RecyclerView.GONE) {
                recyclerView.visibility = RecyclerView.VISIBLE
                icon.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
            } else {
                recyclerView.visibility = RecyclerView.GONE
                icon.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
            }
        }
    }


    private fun loadMySearches() {
        // Initialize data.
        val datasourceFirebaseSearches = Datasource_Firebase_Searches()


        val recyclerView = findViewById<RecyclerView>(R.id.my_searches_list)
        val searchesAdapter = SearchesAdapter(this, emptyList())
        // Create an empty adapter initially
        recyclerView.adapter = searchesAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set the item click listener for the events adapter
        datasourceFirebaseSearches.loadMySearches { searches ->
            val recyclerView = findViewById<RecyclerView>(R.id.my_searches_list)
            recyclerView.adapter = SearchesAdapter(this, searches).apply {
                setOnItemClickListener(object : SearchesAdapter.OnItemClickListener {
                    override fun onItemClick(search: Search) {
                        val intent =
                            Intent(this@SearchesListActivity, SearchDetailsActivity::class.java)
                        intent.putExtra("search", search)
                        startActivity(intent)

                    }
                })
            }
            recyclerView.setHasFixedSize(true)
        }

        datasourceFirebaseSearches.loadMySearches { searches ->
            searchesAdapter.dataset = searches // Update the dataset in the adapter
            searchesAdapter.notifyDataSetChanged() // Notify the adapter that the data has changed
            val my_searches_title = findViewById<TextView>(R.id.my_searches_title)
            my_searches_title.text = "My Searches (${searches.size})"
        }
    }

    private fun loadMyPastSearches() {
        // Initialize data.
        val datasourceFirebaseSearches = Datasource_Firebase_Searches()


        val recyclerView = findViewById<RecyclerView>(R.id.past_searches_list)
        val searchesAdapter = SearchesAdapter(this, emptyList())
        // Create an empty adapter initially
        recyclerView.adapter = searchesAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set the item click listener for the events adapter
        datasourceFirebaseSearches.loadMyPastSearches { searches ->
            val recyclerView = findViewById<RecyclerView>(R.id.past_searches_list)
            recyclerView.adapter = SearchesAdapter(this, searches).apply {
                setOnItemClickListener(object : SearchesAdapter.OnItemClickListener {
                    override fun onItemClick(search: Search) {
                        val intent =
                            Intent(this@SearchesListActivity, SearchDetailsActivity::class.java)
                        intent.putExtra("search", search)
                        startActivity(intent)

                    }
                })
            }
            recyclerView.setHasFixedSize(true)
        }

        datasourceFirebaseSearches.loadMyPastSearches { searches ->
            searchesAdapter.dataset = searches // Update the dataset in the adapter
            searchesAdapter.notifyDataSetChanged() // Notify the adapter that the data has changed
            val past_searches_title = findViewById<TextView>(R.id.past_searches_title)
            past_searches_title.text = "Past Searches (${searches.size})"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}