package com.mikuw.coupler

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mains)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Main"

        // Do any additional setup for your activity here

    }
}
