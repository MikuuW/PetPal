import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.mikuw.coupler.R
import com.mikuw.coupler.model.Petsitter
import com.mikuw.coupler.model.Search
import com.squareup.picasso.Picasso
import java.util.*
import java.util.concurrent.TimeUnit

class PetsitterAdapter(
    private val context: Context,
    var dataset: List<Petsitter>
) : RecyclerView.Adapter<PetsitterAdapter.ItemViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val tv_name: TextView = view.findViewById(R.id.tv_petsitter_name)
        val tv_city: TextView = view.findViewById(R.id.tv_petsitter_city)
        val iv_image: ImageView = view.findViewById(R.id.iv_petsitter_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_petsitter, parent, false)
        return ItemViewHolder(adapterLayout)
    }


    override fun getItemCount(): Int {
        return dataset.size
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        val fullname = item.firstname + " " + item.lastname
        holder.tv_name.text = fullname
        holder.tv_city.text = item.city

        Picasso.get()
            .load(item.imageUri)
            .resize(200, 200)
            .centerCrop()
            .into(holder.iv_image)

        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(item)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(petsitter: Petsitter)
    }
}
