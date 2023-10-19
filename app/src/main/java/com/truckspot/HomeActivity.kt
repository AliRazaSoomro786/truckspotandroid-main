package com.truckspot
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.truckspot.LoginActivity
import com.truckspot.MyDialogReceiver
import com.truckspot.R
import com.truckspot.utils.CheckPermission
import com.truckspot.utils.CheckPermission.Companion.RC_LOCATION_PERMISSION
import java.util.Calendar

@SuppressLint("CustomSplashScreen")
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)




showDialog()

    // Your existing HomeActivity initialization code here.

        // Check for location permission and request if needed.
//        if (CheckPermission.checkIsMarshMallowVersion()) {
//            if (!CheckPermission.checkLocationPermission(this@HomeActivity)) {
//                CheckPermission.requestLocationPermission(this@HomeActivity)
//            } else {
//                moveTONextScreen()
//                finish()
//            }
//        }
//
//        // Set up the alarm manager to trigger the BroadcastReceiver daily at 1:50 PM
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val alarmIntent = Intent(this, MyDialogReceiver::class.java).let { intent ->
//            PendingIntent.getBroadcast(
//                this,
//                0,
//                intent,
//                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        }
//
//        val calendar = Calendar.getInstance()
//        calendar.set(Calendar.HOUR_OF_DAY, 18)
//        calendar.set(Calendar.MINUTE, 32)
//        calendar.set(Calendar.SECOND, 0)
//
//        val currentTimeMillis = System.currentTimeMillis()
//        if (calendar.timeInMillis <= currentTimeMillis) {
//            // If the current time is already past 1:50 PM, set the alarm for the next day
//            calendar.add(Calendar.DAY_OF_YEAR, 1)
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                alarmIntent
//            )
//        } else {
//            alarmManager.setExact(
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                alarmIntent
//            )
//        }
    }
    private fun showDialog() {
        val alertDialog = AlertDialog.Builder(this)

        // Set the title and message for your dialog
        alertDialog.setTitle("Welcome to HomeActivity")
        alertDialog.setMessage("This is a sample dialog in HomeActivity")

        // Set a positive button and its click listener
        alertDialog.setPositiveButton("OK") { dialog, _ ->
            // Close the dialog when the "OK" button is clicked
            dialog.dismiss()
        }

        // Create and show the dialog
        alertDialog.create().show()
    }
}

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            RC_LOCATION_PERMISSION -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    moveTONextScreen()
//                } else {
//                    permissionsRequestPopup()
//                }
//            }
//            else -> {}
//        }
//    }
//
//
//    private fun moveTONextScreen() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }, 3000)
//    }
//
//    private fun permissionsRequestPopup() {
//        val alertDialog = AlertDialog.Builder(this)
//
//        // Setting Dialog Title
//        alertDialog.setTitle("Turn On GPS")
//        alertDialog.setCancelable(false)
//
//        // Setting Dialog Message
//        alertDialog.setMessage("Please turn on GPS")
//
//        // On pressing Settings button
//        alertDialog.setPositiveButton(
//            "Settings"
//        ) { dialog, which ->
//            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            val uri = Uri.fromParts("package", packageName, null)
//            intent.data = uri
//            startActivity(intent)
//        }
//
//        alertDialog.setNegativeButton(
//            "Cancel"
//        ) { dialog, which ->
//            dialog.cancel()
//            finishAffinity()
//        }
//
//        // Showing Alert Message
//        alertDialog.show()
//    }

    // Rest of your HomeActivity code...
