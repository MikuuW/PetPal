package com.mikuw.coupler

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.model.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageDetailsActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_details)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Message Details"

        // BURGER MENU / NAVIGATION DRAWER
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

        if (message != null) {
            handleAnswerButton(message)
        }


    }

    private fun handleAnswerButton(message: Message) {
        val btn_answer = findViewById<TextView>(R.id.btn_message_details)
        btn_answer.setOnClickListener {
            val intent = Intent(this, MessageWriteActivity::class.java)
            intent.putExtra("receiverUid", message.sender)
            intent.putExtra("title", message.title)
            startActivity(intent)
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
                    tv_sender.text = "Message from $firstname $lastname"
                } else {
                    // Handle the case where the document does not exist
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during the retrieval
            }

        val timestamp = message.timestamp
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        val date = Date(timestamp)
        val formattedDate = dateFormat.format(date)
        val formattedTime = timeFormat.format(date)

        val formattedTimestamp = "$formattedDate at $formattedTime"
        tv_time.text = formattedTimestamp

        tv_content.text = message.content
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}