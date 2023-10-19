package com.truckspot.request

data class AddLogRequest(
    val modename: String,
    val odometerreading: String,
    val eng_hours: String,
    val is_autoinsert:Int,
    val location:String?="Location Not Available",
    val eventtype:Int,
    val shipping_number:String,
    val trailer_number:String,
    val vin:String

)

data class GetLogResponse(
val authorization_status:String
)
data class AddLogRequestunauth(


    val modename: String,
    val odometerreading: String,
    val eng_hours: String,
    val is_autoinsert:Int



)