import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.mikuw.coupler.R
import com.mikuw.coupler.model.Pet
import com.squareup.picasso.Picasso

class ShowPetsAdapter(
    private val context: Context,
    private val dataset: List<Pet>
) : RecyclerView.Adapter<ShowPetsAdapter.ItemViewHolder>() {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(pet: Pet)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val tv_title: TextView = view.findViewById(R.id.tv_search_title)
        val iv_image: ImageView = view.findViewById(R.id.iv_show_pet_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_show_pets, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.tv_title.text = item.name

        // Load image with Picasso
        val storageRef = FirebaseStorage.getInstance().reference.child("images_pet/${item.ownerId}/${item.name}.jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get()
                .load(uri)
                .resize(400, 400)
                .centerCrop()
                .into(holder.iv_image)
        }

        holder.itemView.setOnClickListener {
            listener?.onItemClick(item)
        }
    }

}
