package com.mikuw.coupler

import com.mikuw.coupler.data.Datasource_Firebase_Pets
import com.mikuw.coupler.adapter.ShowPetsAdapter
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mikuw.coupler.model.Pet

class PetsListActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recyclerView: RecyclerView
    private lateinit var petsAdapter: ShowPetsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets_list)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Your Pets"

        // BURGER MENU / NAVIGATION DRAWER
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)

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
                        val intent =
                            Intent(this@PetsListActivity, PetProfileShowActivity::class.java)
                        intent.putExtra("pet", pet)
                        startActivity(intent)
                    }
                })
            }

            recyclerView.setHasFixedSize(true)
        }
        handleNotLoggedInUser()
    }

    private fun handleNotLoggedInUser() {
        // make visible
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        val textView = findViewById<TextView>(R.id.tv_pet_list_not_logged_in)
        val button = findViewById<TextView>(R.id.btn_pet_list_not_logged_in)
        val layout = findViewById<DrawerLayout>(R.id.tv_edit_image)



        if (!isLoggedIn) {
            layout.setBackgroundColor(Color.parseColor("#b1a7a6"))

            textView.visibility = TextView.VISIBLE
            button.visibility = TextView.VISIBLE
            button.setOnClickListener {
                val intent = Intent(this, UserLoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
