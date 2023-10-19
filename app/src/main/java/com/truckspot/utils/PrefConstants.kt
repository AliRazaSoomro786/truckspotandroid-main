package com.truckspot.utils

object PrefConstants {
    /*     val allowedOnEngine=0.233
         val allowedTruckDrive=0.183
         val allowedRefreshment=0.05
         val allowedContinousDrivingLimit=0.133
         val allowedOffEngine=0.166
         val weekWiseDriveLimit=1.0
         val weekWiseRestLimit=0.566*/
    val allowedOnEngine = 14
    val allowedTruckDrive = 11
    val allowedRefreshment = 3
    val allowedOffEngine = 10
    val weekWiseDriveLimit = 60
    val weekWiseRestLimit = 34
    val TRACKER_DRIVER = "TRACKER_DRIVER"

    val allowedContinousDrivingLimit = 8.0 // (6 minutes ) todo change this with 8
    const val MAX_DRIVING_HOURS = 8.0 // (6 minutes) todo change this with 8 hours
    const val DRIVING_PER_DAY = 11.0 // (12 minutes ) todo change with 11
    const val DUTY_PER_DAY = 14.0 // (18 minutes )todo change with 14.0
    const val EIGHT_DAY_PERIOD = 70.0


    const val TRUCK_MODE_ON = "on"
    const val TRUCK_MODE_OFF = "off"
    const val TRUCK_MODE_DRIVING = "d"
    const val TRUCK_MODE_SLEEPING = "sb"
}