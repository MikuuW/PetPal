package com.mikuw.coupler

import EventsAdapter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mikuw.coupler.data.Datasource_Firebase_Events
import com.mikuw.coupler.model.Pet

class PetProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_profile)


        val pet = intent.getSerializableExtra("pet") as? Pet
        val name = pet?.name
        val desc = pet?.desc


        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "$name's Profile"

        val tv_pet_desc = findViewById<TextView>(R.id.et_pet_desc)
        tv_pet_desc.text = desc
        println(desc)
    }

}
