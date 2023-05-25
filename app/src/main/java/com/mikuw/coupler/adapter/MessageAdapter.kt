package com.mikuw.coupler.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikuw.coupler.R
import com.mikuw.coupler.model.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val senderTextView: TextView = itemView.findViewById(R.id.textViewSender)
        private val timestampTextView: TextView = itemView.findViewById(R.id.textViewTimestamp)

        fun bind(message: Message) {
            senderTextView.text = message.sender

            val timestamp = message.timestamp
            val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

            val date = Date(timestamp)
            val formattedDate = dateFormat.format(date)
            val formattedTime = timeFormat.format(date)

            val formattedTimestamp = "$formattedDate at $formattedTime"
            timestampTextView.text = formattedTimestamp
        }
    }

}


