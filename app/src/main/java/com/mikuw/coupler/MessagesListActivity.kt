package com.mikuw.coupler

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.mikuw.coupler.adapter.MessageAdapter
import com.mikuw.coupler.data.Datasource_Firebase_Messages
import com.mikuw.coupler.model.Message

class MessagesListActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Messages"

        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        // WEITER

        // Setup RecyclerView
        loadMessages()
    }

    private fun loadMessages() {
        // Initialize data.
        val datasourceFirebaseMessages = Datasource_Firebase_Messages()

        val recyclerView = findViewById<RecyclerView>(R.id.rv_messages)
        val messagesAdapter = MessageAdapter(this, emptyList())
        recyclerView.adapter = messagesAdapter

        // Load the messages from the data source
        datasourceFirebaseMessages.loadMessages { messages ->
            val sortedMessages = messages.sortedByDescending { it.timestamp }
            messagesAdapter.dataset = sortedMessages
            messagesAdapter.notifyDataSetChanged()
        }

        // Set the item click listener for the events adapter
        messagesAdapter.setOnItemClickListener(object : MessageAdapter.OnItemClickListener {
            override fun onItemClick(message: Message) {
                val intent = Intent(this@MessagesListActivity, MessageDetailsActivity::class.java)
                intent.putExtra("message", message)
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
