package com.mikuw.coupler

import Datasource_Firebase_Pets
import ShowPetsAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.mikuw.coupler.model.Pet

class PetsShowActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets_show)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Your Pets"

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        //TEST BURGER MENU
        // Initialize data.
        val datasourceFirebasePets = Datasource_Firebase_Pets()

        datasourceFirebasePets.loadPets { pets ->
            val recyclerView = findViewById<RecyclerView>(R.id.rv_show_pets)
            recyclerView.adapter = ShowPetsAdapter(this, pets).apply {
                setOnItemClickListener(object : ShowPetsAdapter.OnItemClickListener {
                    override fun onItemClick(pet: Pet) {
                        val intent = Intent(this@PetsShowActivity, PetProfileShowActivity::class.java)
                        intent.putExtra("pet", pet)
                        startActivity(intent)
                    }
                })
            }

            recyclerView.setHasFixedSize(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

