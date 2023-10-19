package com.truckspot.models

import java.util.Date

data class GetLogsResponse(
    val code: Int,
    val message: String,
    val results: ResultsNew,
    val status: Boolean
)

data class GetDatesResponses (
    val dates: List<String>
        )
data class ResultsNew(
    val totalCount: Int,
    val currentTime: String,
    val queryDate: String,
    val userLogs: List<UserLog>
)
data class UserLog(
    val id: Int,
    val created_on: String,
    val date: String,
    val datetime: String,
    val discreption: Any,
    val eng_hours: String,
    val is_autoinsert: String,
    val location: Any,
    val modename: String,
    val hours: Double=0.0,
    val end_DateTime: String? = null, // Make end_DateTime nullable
    val odometerreading: String,
    val time: String,
    val timesheet: String,
    val vin: Any,
    val authorization_status:String

)