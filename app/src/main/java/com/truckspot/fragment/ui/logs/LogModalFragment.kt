import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.truckspot.R
import com.truckspot.fragment.ui.home.HomeViewModel
import com.truckspot.models.UserLog
import com.truckspot.request.updateLogRequest
import hilt_aggregated_deps._com_truckspot_fragment_ui_home_HomeViewModel_HiltModules_KeyModule
import okhttp3.internal.notify

class LogModalFragment(private val userLog: UserLog) : DialogFragment() {
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.log_modal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        val editTextTime = view.findViewById<EditText>(R.id.editTextTime)
        editTextTime.setText(userLog.time)

        val editTextModename = view.findViewById<EditText>(R.id.editTextModename)
        editTextModename.setText(userLog.modename)

        val editTextLocation = view.findViewById<EditText>(R.id.editTextlocation)
//        editTextLocation.setText(userLog.location)

        val editTextOdometer = view.findViewById<EditText>(R.id.editTextodometer)
        editTextOdometer.setText(userLog.odometerreading)

        val editTextEngineHours = view.findViewById<EditText>(R.id.editTextenginehours)
        editTextEngineHours.setText(userLog.eng_hours)


        val btnEdit = view.findViewById<Button>(R.id.btnEdit)

        btnEdit.setOnClickListener {
            val updatedTime = editTextTime.text.toString()
            val updatedModename = editTextModename.text.toString()
            val updatedLocation = editTextLocation.text.toString()
            val updatedOdometer = editTextOdometer.text.toString()
            val updatedEngineHours = editTextEngineHours.text.toString()

            // Create an UpdatedLogData object
            val updatedLogData = updateLogRequest(
                userLog.id,
                0.0,
                userLog.end_DateTime,
                updatedModename,
                updatedLocation,
                updatedOdometer,
                updatedEngineHours,
                updatedTime,
                1
            )

            try {
                val resp = homeViewModel.updateLog(updatedLogData)
                Log.v("checkupdateapi", "Update successful")

                // Close the dialog
                dismiss()

                // Display a toast message
                Toast.makeText(requireContext(), "Log updated successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Handle the error here, you can log the error message or return an error message
                Log.e("API Error", "Error updating log: ${e.message}", e)

                // Return an error message or code, depending on your needs
                Toast.makeText(requireContext(), "Error updating log: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun updateLog(updatedLogData: updateLogRequest, homeViewModel: HomeViewModel): String {
    try {
        homeViewModel.updateLog(updatedLogData)

         return "Updated successfully"
    } catch (e: Exception) {
        // Handle the error here, you can log the error message or return an error message
        Log.e("API Error", "Error updating log: ${e.message}", e)

        // Return an error message or code, depending on your needs
        return "Error updating log: ${e.message}"
    }
}

