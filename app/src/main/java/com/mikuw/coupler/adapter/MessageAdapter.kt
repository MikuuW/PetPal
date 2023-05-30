package com.mikuw.coupler.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.mikuw.coupler.R
import com.mikuw.coupler.model.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val context: Context,
    var dataset: List<Message>
) : RecyclerView.Adapter<MessageAdapter.ItemViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val tv_sender: TextView = view.findViewById(R.id.textViewSender)
        val tv_time: TextView = view.findViewById(R.id.textViewTimestamp)
        val tv_title: TextView = view.findViewById(R.id.textViewTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_message, parent, false)
        return ItemViewHolder(adapterLayout)
    }


    override fun getItemCount(): Int {
        return dataset.size
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        if(!item.isRead) {
            holder.tv_title.setTypeface(null, Typeface.BOLD)
        }
        holder.tv_title.text = item.title

        val timestamp = item.timestamp
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        val date = Date(timestamp)
        val formattedDate = dateFormat.format(date)
        val formattedTime = timeFormat.format(date)

        val formattedTimestamp = "$formattedDate at $formattedTime"
        holder.tv_time.text = formattedTimestamp

        // get the field firstname and lastname from the document in firestore where document id is equal to the sender
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(item.sender)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val firstname = documentSnapshot.getString("firstname")
                    val lastname = documentSnapshot.getString("lastname")

                    // Use the firstname and lastname values
                    // e.g., display them in a TextView

                    holder.tv_sender.text = "$firstname $lastname"
                } else {
                    // Handle the case where the document does not exist
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred during the retrieval
            }




        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(item)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(message: Message)
    }
}
