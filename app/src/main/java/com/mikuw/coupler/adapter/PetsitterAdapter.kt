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
import com.squareup.picasso.Picasso
import java.util.*
import java.util.concurrent.TimeUnit

class PetsitterAdapter(
    private val context: Context,
    private val dataset: List<Petsitter>
) : RecyclerView.Adapter<PetsitterAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val tv_name: TextView = view.findViewById(R.id.tv_petsitter_name)
        val tv_city: TextView = view.findViewById(R.id.tv_petsitter_city)
        val iv_image: ImageView = view.findViewById<ImageView>(R.id.iv_petsitter_image)
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
        holder.tv_name.text = item.name
        holder.tv_city.text = item.location

        holder.iv_image.layoutParams.width = 200
        holder.iv_image.layoutParams.height = 200
        // TODO: Funzt noch nicht
        Picasso.get()
            .load(item.imageUrl)
            .resize(200, 200)
            .centerCrop()
            .into(holder.iv_image)
        println("HERE: " + item.imageUrl)

        //url holen
        val db = FirebaseStorage.getInstance()
        val storageRef = db.reference.child("images_pet/og7wAO7u0JTIHTKhix0N6Nbuhfi1/Brownie.jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            println("NEW ONE: " + uri)
            Picasso.get().load(uri).resize(400, 400).centerCrop().into(holder.iv_image)
        }
    }
}
