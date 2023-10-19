package com.truckspot.fragment.ui.certify

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.truckspot.R
import com.truckspot.api.TruckSpotAPI
import com.truckspot.models.CertifyModelItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CertifyFragment : Fragment() {
    private lateinit var truckSpotAPI: TruckSpotAPI // Retrofit service
    private lateinit var dateContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_certify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateContainer = view.findViewById(R.id.dateContainer)
        initRetrofit()
        getData()

    }

    private fun initRetrofit() {
        // Create a Retrofit instance
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://dev-api.truckspoteld.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create a service for the TruckSpotAPI interface
        truckSpotAPI = retrofitBuilder.create(TruckSpotAPI::class.java)
    }

    private fun getData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = truckSpotAPI.getDates()
                if (response.isSuccessful) {
                    val certifyModelItems = response.body()
                    if (certifyModelItems != null) {
                        requireActivity().runOnUiThread {
                            renderDates(certifyModelItems)
                        }
                    }
                } else {
                    Log.d("Failmessage", "Failed")

                    // Log the error response data
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("ErrorResponse", errorBody)

                    // Handle error response or throw an exception
                }
            } catch (e: Throwable) {
                Log.e("Exception", "Error: ${e.message}", e)

                // Handle exceptions
            }
        }
    }


    //    private fun updateCertified(dates: List<CertifyModelItem>){
//
//
//    }
    private fun renderDates(dates: List<CertifyModelItem>) {
        for (dateItem in dates) {
            // Create a horizontal LinearLayout for each row
            val linearLayout = LinearLayout(requireContext())
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayout.gravity = Gravity.CENTER_VERTICAL // Center vertically

            // Add margin top to the entire row
            val marginLayoutParams = linearLayout.layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.topMargin = resources.getDimensionPixelSize(R.dimen._80dp)
            linearLayout.layoutParams = marginLayoutParams

            // Create the button
            val button = Button(requireContext())
            val buttonLayoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.dp_90dp),
                resources.getDimensionPixelSize(R.dimen._40dp)
            )
            button.layoutParams = buttonLayoutParams
            button.text = "Certify" // Text on the button

            // Create the TextView for the date
            val textView = TextView(requireContext())
            textView.text = dateItem.date
            textView.setTextColor(resources.getColor(R.color.white))
            textView.textSize = resources.getDimension(R.dimen._10dp) // Adjust text size as needed

            // Add any additional styling or formatting as needed

            // Add the button and text view to the linear layout
            linearLayout.addView(button)
            linearLayout.addView(textView)

            // Add margin between button and text view
            val buttonMarginLayoutParams = button.layoutParams as ViewGroup.MarginLayoutParams
            buttonMarginLayoutParams.marginEnd = resources.getDimensionPixelSize(R.dimen._40dp)
            button.layoutParams = buttonMarginLayoutParams

            // Set a click listener for the button to call the API
            button.setOnClickListener {
                updateCertified(dateItem.date)
            }

            // Add the row to the dateContainer
            dateContainer.addView(linearLayout)
        }
    }


    private fun updateCertified(date: String) {
        // Create an instance of updateCertified with the appropriate data
        val updateCertifiedRequest = CertifyModelItem(date, true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = truckSpotAPI.updatedCertified(updateCertifiedRequest)
                if (response.isSuccessful) {
                    // Handle a successful response
                    Log.d("updatesuccess", "Certification status updated successfully")
                } else {
                    // Handle API errors or unsuccessful response
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("updateerrors", "Certification status update failed: $errorBody")
                }
            } catch (e: Exception) {
                // Handle network or API call exception
                Log.e("API Exception", "Error calling API: ${e.message}")
            }
        }
    }
}