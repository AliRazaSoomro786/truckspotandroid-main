package com.truckspot.utils

import android.util.Log
import com.truckspot.models.UserLog
import com.truckspot.utils.PrefConstants.TRUCK_MODE_DRIVING
import com.truckspot.utils.PrefConstants.TRUCK_MODE_ON
import org.joda.time.*
import java.text.DecimalFormat
import java.util.*


object AlertCalculationUtils {
    private const val TAG = "AlertCalculationUtils"

    fun calculate(userLogs: List<UserLog>): Pair<Double, Double> {
        try {
            val lastOnMode = latestOnMode(userLogs)
            if (lastOnMode != null) Log.d(TAG, "lastOnMode $lastOnMode")

            val nextObjects = userLogs.subList(userLogs.indexOf(lastOnMode), userLogs.size)
            Log.d(TAG, "Next objects count is ${nextObjects.size}")

            val next14Hours = next14Hours(nextObjects)
            Log.d(TAG, "next14Hours $next14Hours")
            return Pair(next14Hours.first, next14Hours.second)
        } catch (e: Exception) {
            return Pair(0.0, 0.0)
        }

    }

    fun calculate(userLogs: List<UserLog>, mode: String): Double {
        return overallHours(userLogs, mode)
    }

    private fun overallHours(userLogs: List<UserLog>, mode: String): Double {
        if (userLogs.isEmpty()) return 0.0
        val lastElement = userLogs.last()

        var totalHours = 0.0

        for (index in userLogs.indices) {
            val logs = userLogs[index]

            val hasNextLog = hasNext(index, userLogs)

            if (logs.modename == mode) {
                if (hasNextLog != null) {
                    totalHours += getDifferenceInHours(logs.time, hasNextLog.time)
                } else {
                    totalHours += getHours(lastElement.timesheet)
                    Log.d(TAG, "last mode found ${logs.modename}")
                }
            }

        }

        Log.d(TAG, "overallHours $totalHours")
        return totalHours
    }


    fun continuousDriving(userLogs: List<UserLog>): Double {
        return try {
            val logs = userLogs.last()
            if (logs.modename == TRUCK_MODE_DRIVING) {
                getHours(logs.timesheet)
            } else 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    private fun next14Hours(userLogs: List<UserLog>): Pair<Double, Double> {
        if (userLogs.isEmpty()) return Pair(0.0, 0.0)
        val lastElement = userLogs.last()

        var totalHours = 0.0
        var driveHours = 0.0

        for (index in userLogs.indices) {
            val logs = userLogs[index]

            val hasNextLog = hasNext(index, userLogs)

            if (logs.modename == TRUCK_MODE_ON || logs.modename == TRUCK_MODE_DRIVING) {
                if (hasNextLog != null) {
                    totalHours += getDifferenceInHours(logs.time, hasNextLog.time)

                    if (logs.modename == TRUCK_MODE_DRIVING) {
                        driveHours += getDifferenceInHours(logs.time, hasNextLog.time)
                    }
                    Log.d(TAG, "calculated time ---> $totalHours")
                } else {
                    totalHours += getHours(lastElement.timesheet)
                    if (logs.modename == TRUCK_MODE_DRIVING) {
                        driveHours += getHours(lastElement.timesheet)
                    }

                    Log.d(TAG, "last mode found ${logs.modename}")
                }
            }

        }

        Log.d(TAG, "next14Hours total time spent so far $totalHours")
        Log.d(TAG, "next14Hours total time spent in drive $driveHours")

        return Pair(totalHours, driveHours)
    }

    private fun hasNext(currentIndex: Int, userLogs: List<UserLog>): UserLog? {
        return if (currentIndex < userLogs.size - 1) {
            userLogs[currentIndex + 1]
        } else {
            null
        }
    }

    private fun latestOnMode(userLogs: List<UserLog>): UserLog? {
        for (log in userLogs) if (log.modename == TRUCK_MODE_ON) return log
        return null
    }

    fun getHours(timeSheet: String): Double {
        return try {

            val localTime = DateTime()
            val pacificTimeZone = DateTimeZone.forID("America/Los_Angeles")
            val currentTime = localTime.withZone(pacificTimeZone)

            val givenDateTime = DateTime(timeSheet)

            val timeDifference = Period(givenDateTime.toLocalTime(), currentTime.toLocalTime())

            val hours = timeDifference.hours
            val minutes = timeDifference.minutes
            Log.d(TAG, "hours : $hours minutes : $minutes")

            val decimalFormat = DecimalFormat("#.#")
            return decimalFormat.format(hours + (minutes / 60.0)).toDouble()
        } catch (e: Exception) {
            0.0
        }


    }

    fun getHoursIntoMinutes(time: Double): String {
        return try {
            val hours = time.toInt()
            val minutesDecimal = time - hours
            val minutes = (minutesDecimal * 60).toInt()
            "$hours:$minutes"
        }catch (e : Exception){
            decimalHours(time).toString()
        }

    }

    fun getDifferenceInHours(startTimeStr: String, endTimeStr: String): Double {
        val startTime = LocalTime.parse(startTimeStr)
        val endTime = LocalTime.parse(endTimeStr)

        val hours = Hours.hoursBetween(startTime, endTime).hours
        val minutes = Minutes.minutesBetween(startTime, endTime).minutes % 60

        val totalHours = hours + (minutes.toDouble() / 60.0)
        Log.d(TAG, "hours ---> $totalHours")

        return totalHours
    }


    fun getDifferenceInHours(previousDate: Date, newDate: Date): Double {
        val diff: Long = newDate.time - previousDate.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        Log.d(TAG, "minutes date ----> $minutes")
        return minutes / 60.0
    }

    fun decimalHours(hours: Double): Double {
        val decimalFormat = DecimalFormat("#.#")
        return decimalFormat.format(hours).toDouble()

    }

    fun decimalLocation(location: String): Double {
        return location.toDouble()
    }


    /* todo Driving per day */
    /*This violation will hit when driver spent more than 11 hrs in his first 14 hrs.
    * ON duty Mode start ----> 14 hours total time and drive hours should be less than 11 hours
    * */
    /*{
    *   first calculate 14 hours in on mode
    *  ---> First ON mode get timesheet and compare with next modes timesheet till 14 hours
    * ----> Start loop on mode to 14 hours --->
    * }
    * */

    /* todo Duty per day */
//       /* 24 hours me 14 hours on duty me reh skta hai*/
    // 1 : -14 hours me only 11 hours allow hai
    // 2 : -if driver cross more than 14 hours in on duty mode then violation will hit && if cross 11 hours in drive mode
    // 3: - ON duty mode 14 hours then driver will spent 10 hours in sleep mode


    /*This is total time a driver spent in drive,off duty, on duty, sleep.
    If it crosses 14 hrs it will.show violation. Bcz for resetting a book a driver needs 10.hrs
    continues sleep. If he takes sleep in brk and less than 10 hrs they will not count as sleep
    or it will show violation if he doesn't sleep for 10 hrs straight in 24 hrs.
     For example if shahzad starts his book at 5am and driver for 7 hrs 59 mint he needs
      to take a brk for 30 mint in any mode other then drive. Otherwise 8 hrs violation
      will show..he can still driver 3 more hours but suppose he he drive for 3hrs 10 mint
      his 11 hrs violation will show or  in other case suppose he don't drive but put it
      in off duty or sleep or on duty for 6 hrs he will show 14 hrs violation bcz total
      time is 14hrs 30 mint untill he put it in sleep for 6 hrs and continue it to 10 hrs
      straight and then thar will reset his log book for another 24 hrs. Remember
      whenever he put his book in sleep only in sleep for straight 10.hrs
      sleep his book reset again.*/

    /* todo 8 Day period */
    /*This violation will show when driver total time in drive and on duty count more than 70.hrs.
     it can be in 5 days or 10 days or 30 days .doesn't matter. Once he completes his total time
      in drive and on duty for 70 hrs he has to put his book in off duty for 34 hrs straight for
      reset another 70 hrs drive/on duty book. Now suppose driver use book for driver for 4 days
      and use only 40.hrs and then take 34 hrs off duty that means he lost his 70-40=30 hrs..
      his book is reserved again for next 70.hrs*/
}