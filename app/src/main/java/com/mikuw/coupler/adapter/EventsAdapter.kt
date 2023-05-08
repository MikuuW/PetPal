import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikuw.coupler.R
import com.mikuw.coupler.model.Event

class EventsAdapter(
    private val context: Context,
    private val dataset: List<Event>
) : RecyclerView.Adapter<EventsAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val tv_title: TextView = view.findViewById(R.id.tv_search_title)
        val tv_date: TextView = view.findViewById(R.id.tv_search_date)
        val tv_location: TextView = view.findViewById(R.id.tv_search_city)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_events, parent, false)

        return ItemViewHolder(adapterLayout)
    }


    override fun getItemCount(): Int {
        return dataset.size
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.tv_title.text = item.name
        holder.tv_date.text = item.formattedDate(item.from!!) + " - " + item.formattedDate(item.to!!)
        holder.tv_location.text = item.location
    }

}
