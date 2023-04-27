package com.mikuw.coupler

import Datasource_Firebase_Pets
import ShowPetsAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mikuw.coupler.model.Pet

class MyPetsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_pets)

        // Initialize data.
        val datasourceFirebasePets = Datasource_Firebase_Pets()

        datasourceFirebasePets.loadPets { pets ->
            val recyclerView = findViewById<RecyclerView>(R.id.rv_show_pets)
            recyclerView.adapter = ShowPetsAdapter(this, pets).apply {
                setOnItemClickListener(object : ShowPetsAdapter.OnItemClickListener {
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

