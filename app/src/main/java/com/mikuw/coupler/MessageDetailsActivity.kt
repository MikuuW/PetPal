package com.mikuw.coupler

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.mikuw.coupler.model.Message
import com.mikuw.coupler.model.Petsitter

class MessageDetailsActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_details)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Message Details"

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        // WEITER
        val message = intent.getSerializableExtra("message") as? Message


        if (message != null) {
            displayMessage(message)
        }



    }

    private fun displayMessage(message: Message) {
        //views holen
        val tv_sender = findViewById<TextView>(R.id.tv_msg_details_sender)
        val tv_time = findViewById<TextView>(R.id.tv_msg_details_time)
        val tv_content = findViewById<TextView>(R.id.tv_msg_details_content)

        tv_sender.text = message.sender
        tv_time.text = message.timestamp.toString()
        tv_content.text = message.content
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}