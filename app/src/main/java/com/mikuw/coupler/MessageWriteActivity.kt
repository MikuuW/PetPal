package com.mikuw.coupler

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MessageWriteActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_write)

        // Retrieve the ActionBar object
        val actionBar = supportActionBar
        actionBar?.title = "Write Message"
        //TEST BURGER MENU
        val drawerLayout: DrawerLayout = findViewById(R.id.tv_edit_image)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupNavigationDrawer(this)

        //WEITER
        val receiverUid = intent.getStringExtra("receiverUid")
        val button_send_message = findViewById<Button>(R.id.btn_send_message)

        button_send_message.setOnClickListener() {
            sendMessage(receiverUid)
        }
    }

    private fun sendMessage(receiverUid: String?) {
        val db = FirebaseFirestore.getInstance()
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val tv_content = findViewById<EditText>(R.id.et_msg_write_content)


        val message = hashMapOf(
            "sender" to userID,
            "receiver" to receiverUid,
            "content" to tv_content.text.toString(),
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("messages")
            .add(message)
            .addOnSuccessListener {
            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error sending message!", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}