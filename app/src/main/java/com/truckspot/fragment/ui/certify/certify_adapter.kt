import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.truckspot.R
import com.truckspot.models.CertifyModelItem

class CertifyAdapter(private val items: List<CertifyModelItem>) :
    RecyclerView.Adapter<CertifyAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
//        val certifyTextView: TextView = itemView.findViewById(R.id.certifyTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_certify, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.dateTextView.text = item.date
     }

    override fun getItemCount(): Int {
        return items.size
    }
}
