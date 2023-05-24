import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikuw.coupler.R
import com.mikuw.coupler.model.Search
import java.util.*
import java.util.concurrent.TimeUnit

class SearchesAdapter(
    private val context: Context,
    var dataset: List<Search>
) : RecyclerView.Adapter<SearchesAdapter.ItemViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val tv_title: TextView = view.findViewById(R.id.tv_search_title)
        val tv_date: TextView = view.findViewById(R.id.tv_search_date)
        val tv_date_days: TextView = view.findViewById(R.id.tv_search_date_days)
        val tv_location: TextView = view.findViewById(R.id.tv_search_city)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_search, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.tv_title.text = item.title
        holder.tv_date.text = item.formattedDate(item.from!!) + " - " + item.formattedDate(item.to!!)
        holder.tv_date_days.text = calculateDaysBetweenDates(item.from!!, item.to!!).toString() + " days"
        holder.tv_location.text = item.location


        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(item)
        }
    }

    private fun calculateDaysBetweenDates(fromDate: Date, toDate: Date): Long {
        val diffInMillies = toDate.time - fromDate.time
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
    }

    interface OnItemClickListener {
        fun onItemClick(search: Search)
    }
}
