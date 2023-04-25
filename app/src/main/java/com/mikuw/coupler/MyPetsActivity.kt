package com.mikuw.coupler

import PetsAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mikuw.coupler.data.Datasource_Firebase_Pets
import com.mikuw.coupler.model.Pet

class MyPetsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pets)

        // Initialize data.
        val datasourceFirebaseEvents = Datasource_Firebase_Pets()

        datasourceFirebaseEvents.loadPets { pets ->
            val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
            recyclerView.adapter = PetsAdapter(this, pets).apply {
                setOnItemClickListener(object : PetsAdapter.OnItemClickListener {
                    override fun onItemClick(pet: Pet) {
                        val intent = Intent(this@MyPetsActivity, PetProfileActivity::class.java)
                        intent.putExtra("pet", pet)
                        startActivity(intent)
                    }
                })
            }

            recyclerView.setHasFixedSize(true)
        }
    }
}

