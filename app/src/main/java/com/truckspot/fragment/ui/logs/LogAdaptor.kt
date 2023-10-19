package com.truckspot.fragment.ui.logs

import LogModalFragment
import android.content.Context
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.truckspot.R
import com.truckspot.fragment.ui.home.HomeViewModel
import com.truckspot.models.UserLog
import com.truckspot.request.AddLogRequest
import com.truckspot.utils.AlertCalculationUtils.decimalLocation
import java.io.IOException
import java.util.*

class LogAdaptor  (private val dataSet: List<UserLog>, private val fragmentManager: FragmentManager) :
    RecyclerView.Adapter<LogAdaptor.ViewHolder>() {
    private var previousLog: UserLog? = null

    private lateinit var homeViewModel: HomeViewModel
     private val handler = Handler()

    private val oneMinuteMillis: Long = 60 * 1000L // One minute in milliseconds
    private var lastLogTimestamp: Long = 0

    init {
        // Initialize the lastLogTimestamp to the timestamp of the last log in the dataSet
        lastLogTimestamp = getLastLogTimestamp()

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView
        val tvStatus: TextView
        val tvLocation: TextView
        val tvOdo: TextView
        val tvEng: TextView
        val tvorigon: TextView

        init {
            tvTime = view.findViewWithTag("binding_1")
            tvStatus = view.findViewWithTag("binding_2")
            tvLocation = view.findViewWithTag("binding_3")
            tvOdo = view.findViewWithTag("binding_4")
            tvEng = view.findViewWithTag("binding_5")
            tvorigon = view.findViewWithTag("binding_6")
        }
    }


    private fun getLastLogTimestamp(): Long {
        // Initialize a variable to store the timestamp of the last log entry
        var lastLogTimestamp: Long = 0

        // Iterate through the dataSet to find the last log entry's timestamp
        for (log in dataSet) {
            val logTimestamp = log.time.toLongOrNull() ?: continue // Convert to Long or skip
            if (logTimestamp > lastLogTimestamp) {
                lastLogTimestamp = logTimestamp
            }
        }

        return lastLogTimestamp
    }

//    private fun createLogDataFromPreviousLog(previousLog: UserLog): addLogRequest {
//        // Use the values from the previous log to create a new log
//        return addLogRequest(
//            previousLog.hours,
//            previousLog.end_DateTime,
//            previousLog.modename,
//            previousLog.location,
//            previousLog.odometerreading,
//            previousLog.eng_hours,
//            previousLog.time,
//            previousLog.is_autoinsert
//        )
//    }

//    private fun createDefaultLogData(): AddLogRequest {
//        // Create a new log with default values
//        return AddLogRequest(
//            '0',  // You may need to set the appropriate default values here
//            0.0,
//            "default_end_datetime",
//            "default_modename",
//            "default_location",
//            "default_odometer",
//            "default_engine_hours",
//            "default_time",
//            0
//        )
//    }

    private fun displaySnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    // ... (Other parts of your code remain unchanged)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_log_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener {
            val userLog = dataSet[position]
            val modalFragment = LogModalFragment(userLog)
            modalFragment.show(fragmentManager, "LogModalFragment")
        }

        viewHolder.tvTime.text = dataSet[position].time
        viewHolder.tvStatus.text = dataSet[position].modename.uppercase()
        if (dataSet[position].authorization_status == "not authorized") {
            // Set the background color to red for unauthorized items
            viewHolder.itemView.setBackgroundColor(Color.RED)
        } else {
            viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        try {
            val location = dataSet[position].location
            if (location == null) {
                viewHolder.tvLocation.text = "Loc Not available"
            } else {
                if (dataSet[position].location == "Location Not Available") {
                    Log.d(TAG, "location not available")
                    viewHolder.tvLocation.text = "Loc Not available"
                } else {
                    val latLng = dataSet[position].location.toString().split(",")
                    val address = getAddressFromLatLng(
                        viewHolder.itemView.context,
                        decimalLocation(latLng[0]),
                        decimalLocation(latLng[1])
                    )

                    viewHolder.tvLocation.text = address
                    Log.d(TAG, "Address --> $address")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "onBindViewHolder: ", e)
        }

        viewHolder.tvOdo.text = dataSet[position].odometerreading
        viewHolder.tvEng.text = dataSet[position].eng_hours
        viewHolder.tvorigon.text = if (dataSet[position].is_autoinsert == "1") "Auto" else "Manual"

        // Check if the log is one minute old and the modename is "on"
        if (isLogOlderThanOneMinute(dataSet[position]) && dataSet[position].modename.equals("on", ignoreCase = true)) {
            // Call the addLog API here for testing purposes
            callAddLogAPIForTesting()
        }
    }
    private val TAG = "LogAdaptor"
    private fun getAddressFromLatLng(
        context: Context, latitude: Double, longitude: Double
    ): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addressText = ""

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val sb = StringBuilder()

                for (i in 0 until address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append(", ")
                }

//                sb.append(address.locality).append(", ")
                sb.append(address.adminArea)
//                sb.append(address.countryName)

                addressText = sb.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return addressText
    }



    private fun callAddLogAPIForTesting() {
        // Create an AddLogRequest with sample data for testing
        val logRequest = AddLogRequest(
            "on", // Example modename "on" for testing
            "3000", // Example odometerreading
            "2000", // Example eng_hours
            2, // Example authorization_status (you can change this as needed)
            "Sample location", // Example location (you can change this as needed)
            2, // Example is_autoinsert (you can change this as needed)
            "2".toInt(), // Example additional field (you can change this as needed)
            "2".toInt(), "1C6RREHT5NN451094"
        )

        // Call the addLog API here or log the request data for testing
        // For example:
        // truckSpotAPI.addLog(logRequest)

        // Log the request data for testing
        Log.d(TAG, "Calling Add Log API for Testing with data: $logRequest")
    }
    private fun isLogOlderThanOneMinute(log: UserLog): Boolean {
        // Calculate the time difference between the log's time and the current time
        val logTimeMillis = log.time.toLongOrNull() ?: return false
        val currentTimeMillis = System.currentTimeMillis()
        val timeDifferenceMillis = currentTimeMillis - logTimeMillis

        // Check if the time difference is greater than or equal to one minute
        return timeDifferenceMillis >= oneMinuteMillis
    }
    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}