package com.mikuw.coupler

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Message

class MessageReadActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_read)

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

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(message.sender)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val firstname = documentSnapshot.getString("firstname")
                    val lastname = documentSnapshot.getString("lastname")

                    // Use the firstname and lastname values
                    // e.g., display them in a TextView
                    tv_sender.text = "$firstname $lastname"
                } else {
                    // Handle the case where the document does not exist
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during the retrieval
            }


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