package com.truckspot.fragment.ui.reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.truckspot.R
import com.truckspot.models.UserLog

class ReportsAdapterAdaptor(private val dataSet: List<UserLog>) :
    RecyclerView.Adapter<ReportsAdapterAdaptor.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView
        val tvMode: TextView
        val tvLocation: TextView
        val tvOdomerter: TextView
        val tvHours: TextView


        init {
            // Define click listener for the ViewHolder's View
            tvTime = view.findViewById(R.id.preview_time)
            tvMode = view.findViewById(R.id.preview_mode)
            tvLocation = view.findViewById(R.id.preview_location)
            tvOdomerter = view.findViewById(R.id.preview_odometer)
            tvHours = view.findViewById(R.id.preview_hours)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_reports_view, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element


        viewHolder.tvTime.text = dataSet[position].time
        viewHolder.tvMode.text = dataSet[position].modename.uppercase()
//        viewHolder.tvLocation.text =
//            if (dataSet[position].location.toString() == "0.000000,0.000000") "Loc Not available " else dataSet[position].location.toString()
        viewHolder.tvOdomerter.text = dataSet[position].odometerreading
        viewHolder.tvHours.text = dataSet[position].eng_hours
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}