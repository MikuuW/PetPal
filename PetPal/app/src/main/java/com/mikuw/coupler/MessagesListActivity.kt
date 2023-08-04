package com.mikuw.coupler

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

        // BURGER MENU / NAVIGATION DRAWER
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavigationDrawer(this)
        // WEITER

        // Setup RecyclerView
        loadMessages()
        handleNotLoggedInUser()
    }


    private fun handleNotLoggedInUser() {
        // make visible
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        val textView = findViewById<TextView>(R.id.tv_messages_list_not_logged_in)
        val button = findViewById<TextView>(R.id.btn_messages_list_not_logged_in)
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
                markMessageAsRead(message.content)
                message.isRead = true
                intent.putExtra("message", message)
                startActivity(intent)
            }
        })

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true)
    }

    fun markMessageAsRead(messageContent: String) {
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("messages")
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        collectionRef
            .whereEqualTo("receiver", uid)
            .whereEqualTo("content", messageContent)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documentSnapshot = querySnapshot.documents[0]
                    val documentId = documentSnapshot.id

                    val documentRef = collectionRef.document(documentId)
                    documentRef
                        .update("isRead", true)
                        .addOnSuccessListener {
                            // Update successful
                            Log.d(TAG, "Message marked as read: $documentId")
                        }
                        .addOnFailureListener { e ->
                            // Handle failure
                            Log.e(TAG, "Error marking message as read: $documentId", e)
                        }
                } else {
                    Log.d(TAG, "No matching document found")
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.e(TAG, "Error querying messages", e)
            }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
