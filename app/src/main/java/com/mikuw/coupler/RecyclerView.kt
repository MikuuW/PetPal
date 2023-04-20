import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikuw.coupler.FileItem
import com.mikuw.coupler.R

class FileAdapter(private val fileList: List<FileItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_PARENT = 0
    private val TYPE_CHILD = 1
    private var expandedPosition = -1

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parentLayout: LinearLayout = itemView.findViewById(R.id.parent_layout)
        val parentText: TextView = itemView.findViewById(R.id.parent_text)
        val expandArrow: ImageView = itemView.findViewById(R.id.expand_arrow)
    }

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val childLayout: LinearLayout = itemView.findViewById(R.id.child_layout)
        val childText1: TextView = itemView.findViewById(R.id.child_text_1)
        val childText2: TextView = itemView.findViewById(R.id.child_text_2)
        val childText3: TextView = itemView.findViewById(R.id.child_text_3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_PARENT) {
            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_parent, parent, false)
            ParentViewHolder(itemView)
        } else {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.list_item_child, parent, false)
            ChildViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = fileList[position]
        if (holder.itemViewType == TYPE_PARENT) {
            val parentHolder = holder as ParentViewHolder
            parentHolder.parentText.text = "Row ${position + 1}"
            parentHolder.parentLayout.setOnClickListener {
                if (expandedPosition == position) {
                    expandedPosition = -1
                    notifyItemChanged(position)
                } else {
                    val oldPosition = expandedPosition
                    expandedPosition = position
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(position)
                }
            }
            if (expandedPosition == position) {
                parentHolder.expandArrow.setImageResource(android.R.drawable.arrow_up_float)
            } else {
                parentHolder.expandArrow.setImageResource(android.R.drawable.arrow_down_float)
            }
        } else {
            val childHolder = holder as ChildViewHolder
            childHolder.childText1.text = item.name
            childHolder.childText2.text = item.name
            childHolder.childText3.text = item.name

            // Collapse the child view if it is not expanded
            if (expandedPosition != holder.adapterPosition) {
                childHolder.childLayout.visibility = View.GONE
            } else {
                childHolder.childLayout.visibility = View.VISIBLE
            }

            // Set an OnClickListener to toggle the expanded state of the child view
            childHolder.itemView.setOnClickListener {
                if (expandedPosition == holder.adapterPosition) {
                    expandedPosition = -1
                    childHolder.childLayout.visibility = View.GONE
                    notifyItemChanged(holder.adapterPosition)
                } else {
                    val oldPosition = expandedPosition
                    expandedPosition = holder.adapterPosition
                    if (oldPosition != -1) {
                        notifyItemChanged(oldPosition)
                    }
                    notifyItemChanged(expandedPosition)
                }
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == expandedPosition) {
            TYPE_CHILD
        } else {
            TYPE_PARENT
        }
    }

    override fun getItemCount(): Int {
        return fileList.size
    }
}
