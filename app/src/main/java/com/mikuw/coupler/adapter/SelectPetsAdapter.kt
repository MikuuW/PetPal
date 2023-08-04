import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.mikuw.coupler.R
import com.mikuw.coupler.model.Pet
import com.squareup.picasso.Picasso

class SelectPetsAdapter(
    private val context: Context,
    private val dataset: List<Pet>
) : RecyclerView.Adapter<SelectPetsAdapter.ItemViewHolder>() {

    private val selectedItems = mutableListOf<Pet>()

    interface OnItemClickListener {
        fun onItemClick(pet: Pet)
    }

    var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val tv_title: TextView = view.findViewById(R.id.tv_select_pet_name)
        val iv_image: ImageView = view.findViewById(R.id.iv_select_pet_image)
        val cb_select: CheckBox = view.findViewById(R.id.cb_select_pet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_pets_select, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {

        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.tv_title.text = item.name
        val uri = item.imageUrl
        // Load image with Picasso
        Picasso.get()
            .load(uri)
            .resize(400, 400)
            .centerCrop()
            .into(holder.iv_image)

        holder.itemView.setOnClickListener {
            listener?.onItemClick(item)
        }

        holder.cb_select.isChecked = selectedItems.contains(item)
        holder.cb_select.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.add(item)
            } else {
                selectedItems.remove(item)
            }

        }
    }

    fun getSelectedPets(): List<Pet> {
        return selectedItems.toList()
    }
}
