package com.truckspot.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.truckspot.api.TruckSpotAPI
import com.truckspot.models.GetLogsResponse
import com.truckspot.models.LogResponse
import com.truckspot.request.AddLogRequest
import com.truckspot.request.AddLogRequestunauth
import com.truckspot.request.updateLogRequest
import com.truckspot.utils.Constants.TAG
import com.truckspot.utils.NetworkResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import retrofit2.Response
import javax.inject.Inject


class DashboardRepository @Inject constructor(private val truckSpotAPI: TruckSpotAPI) {
    private val _logResponseLiveData = MutableLiveData<NetworkResult<LogResponse>>()
    val logResponseLiveData: LiveData<NetworkResult<LogResponse>>
        get() = _logResponseLiveData
    private val _GetLogsLiveData = MutableLiveData<NetworkResult<GetLogsResponse>>()
    val getLogsLiveData: LiveData<NetworkResult<GetLogsResponse>>
        get() = _GetLogsLiveData


    suspend fun getLogs(page: Int, pageSize: Int, days: Int) {
        _logResponseLiveData.postValue(NetworkResult.Loading())
        val response = truckSpotAPI.getLogs(page, pageSize, days)
        Log.d(TAG, response.body().toString())
        handleResponseGetLogs(response)
    }

    suspend fun get7DayLogs(page: Int, pageSize: Int, days: Int): Response<GetLogsResponse> {
        _logResponseLiveData.postValue(NetworkResult.Loading())
        return truckSpotAPI.getLogs(page, pageSize, days)
    }


    suspend fun addLog(addLogRequest: AddLogRequest?) {
        _logResponseLiveData.postValue(NetworkResult.Loading())
        if (addLogRequest == null) {
            return
        }
        val response = truckSpotAPI.addLog(addLogRequest)
        Log.d("check the add log being", response.body().toString())
        handleResponse(response)
    }

    suspend fun addLogUnauth(addLogRequest: AddLogRequestunauth) {
        _logResponseLiveData.postValue(NetworkResult.Loading())
        val response = truckSpotAPI.addLogUnauth(addLogRequest) // Assuming you have a separate API function for unauthenticated requests
        Log.d(TAG, response.body().toString())
        handleResponse(response)
    }



    @SuppressLint("LongLogTag")
    suspend fun updateLog(updateLog: updateLogRequest) {
        _logResponseLiveData.postValue(NetworkResult.Loading())
        val response = truckSpotAPI.updateLog(updateLog)
        Log.d("check the updated api being", response.body().toString())
        handleResponse(response)

    }

    fun addLogJava(addLogRequest: AddLogRequest) =
        GlobalScope.future { addLog(addLogRequest) }

    private fun handleResponseGetLogs(response: Response<GetLogsResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _GetLogsLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            //val errorObj = JSONObject(response.errorBody()?.charStream()?.readText())
            _GetLogsLiveData.postValue(NetworkResult.Error("Something Went wrong at API END"))
        } else {
            _GetLogsLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    private fun handleResponse(response: Response<LogResponse>) {
        if (response.isSuccessful && response.body() != null) {
            Log.d(TAG, "handleResponse: ${response.body()}")
            _logResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            //val errorObj = JSONObject(response.errorBody()?.charStream()?.readText())
            Log.w(TAG, "handleResponse error", )
            _logResponseLiveData.postValue(NetworkResult.Error("Something Went wrong at API END"))
        } else {
            _logResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


//    private fun getDayStartELDGraphData(str: String): ELDGraphData {
//        val dateUtils: DateUtils = DateUtils.INSTANCE
//        val of: LocalDateTime = LocalDateTime.m333of(
//            LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
//            LocalTime.MIDNIGHT
//        )
//        Intrinsics.checkNotNullExpressionValue(
//            of,
//            "of(\n            LocalDat…alTime.MIDNIGHT\n        )"
//        )
//        val previousDaysLastDutyLogFromMilli: List<EventLog> =
//            this.eventLogDao.getPreviousDaysLastDutyLogFromMilli(dateUtils.toEpochMilli(of))
//        return if (previousDaysLastDutyLogFromMilli.isEmpty()) {
//            ELDGraphData(
//                DateUtils.INSTANCE.getHour("00:00"),
//                DutyStatus.OFF_DUTY,
//                0,
//                4,
//                null as DefaultConstructorMarker?
//            )
//        } else ELDGraphData(
//            DateUtils.INSTANCE.getHour("00:00"),
//            previousDaysLastDutyLogFromMilli[0].getEventName(),
//            0,
//            4,
//            null as DefaultConstructorMarker?
//        )
//    }
//
//    private fun currentDayELDGraphDataList(str: String): List<ELDGraphData> {
//        val str2: String
//        val f: Float
//        val dateUtils: DateUtils = DateUtils.INSTANCE
//        val charSequence: CharSequence = str
//        val of: LocalDateTime = LocalDateTime.m333of(
//            LocalDate.parse(
//                charSequence,
//                DateTimeFormatter.ofPattern("yyyy-MM-dd")
//            ), LocalTime.MIDNIGHT
//        )
//        Intrinsics.checkNotNullExpressionValue(
//            of,
//            "of(\n            LocalDat…alTime.MIDNIGHT\n        )"
//        )
//        val epochMilli: Long = dateUtils.toEpochMilli(of)
//        val arrayList: MutableCollection<*> = ArrayList<Any?>()
//        for (next in this.eventLogDao.getParticularDayEventLogs(
//            epochMilli,
//            86400000L + epochMilli
//        )) {
//            val eventLog = next as EventLog
//            if (Intrinsics.areEqual(
//                    eventLog.getEventName() as Any?,
//                    DutyStatus.ON_DUTY as Any
//                ) || Intrinsics.areEqual(
//                    eventLog.getEventName() as Any?,
//                    DutyStatus.OFF_DUTY as Any
//                ) || Intrinsics.areEqual(
//                    eventLog.getEventName() as Any?,
//                    "DRIVE" as Any
//                ) || Intrinsics.areEqual(
//                    eventLog.getEventName() as Any?,
//                    DutyStatus.INT as Any
//                ) || Intrinsics.areEqual(
//                    eventLog.getEventName() as Any?,
//                    DutyStatus.SLEEP as Any
//                ) || Intrinsics.areEqual(
//                    eventLog.getEventName() as Any?,
//                    DutyStatus.PERSONAL_USE as Any
//                ) || Intrinsics.areEqual(
//                    eventLog.getEventName() as Any?,
//                    DutyStatus.YARD_MOVE as Any
//                ) || Intrinsics.areEqual(
//                    eventLog.getEventName() as Any?,
//                    DutyStatus.INTERMEDIATE as Any
//                )
//            ) {
//                arrayList.add(next)
//            }
//        }
//        val list: List<EventLog> = arrayList as List<*>
//        val arrayList2: MutableList<ELDGraphData> = ArrayList()
//        for (eventLog2 in list) {
//            val hour: Float =
//                DateUtils.INSTANCE.getHour(DateUtils.INSTANCE.milliToTime(eventLog2.getEventUTCTimestamp()))
//            val eventName: String = eventLog2.getEventName()
//            val id: Long = eventLog2.getId()
//            Intrinsics.checkNotNull(id)
//            arrayList2.add(ELDGraphData(hour, eventName, id))
//        }
//        if (list.isEmpty()) {
//            str2 = getDayStartELDGraphData(str).status
//        } else {
//            str2 = (CollectionsKt.last(list) as EventLog).getEventName()
//        }
//        f = if (Intrinsics.areEqual(
//                LocalDate.parse(charSequence, DateTimeFormatter.ofPattern("yyyy-MM-dd")) as Any,
//                LocalDate.now(
//                    ZoneId.m349of(UserSession.INSTANCE.getTimeZone())
//                ) as Any?
//            )
//        ) {
//            DateUtils.INSTANCE.getHour()
//        } else {
//            DateUtils.INSTANCE.getHour("23:59")
//        }
//        arrayList2.add(ELDGraphData(f, str2, 0, 4, null as DefaultConstructorMarker?))
//        return arrayList2
//    }
//
//    fun getLogDayList(str: String, list: List<String>): List<LogData> {
//        var str2: String
//        var str3: String
//        val str4: String
//        val str5: String
//        Intrinsics.checkNotNullParameter(str, "dateString")
//        Intrinsics.checkNotNullParameter(list, "logDisplaySettings")
//        val dateUtils: DateUtils = DateUtils.INSTANCE
//        val of: LocalDateTime = LocalDateTime.m333of(
//            LocalDate.parse(
//                str,
//                DateTimeFormatter.ofPattern("yyyy-MM-dd")
//            ), LocalTime.MIDNIGHT
//        )
//        Intrinsics.checkNotNullExpressionValue(
//            of,
//            "of(\n            LocalDat…alTime.MIDNIGHT\n        )"
//        )
//        val epochMilli: Long = dateUtils.toEpochMilli(of)
//        val arrayList: MutableList<LogData> = ArrayList<LogData>()
//        val logDayEventLogs: List<EventLog> =
//            this.eventLogDao.getLogDayEventLogs(epochMilli, 86400000L + epochMilli)
//        val lastDutyLogFromMilli: List<EventLog> =
//            this.eventLogDao.getLastDutyLogFromMilli(epochMilli)
//        val arrayList2: MutableCollection<*> = ArrayList<Any?>()
//        for (next in LOG_DISPLAY_SETTING_LIST) {
//            if (!list.contains(next as String)) {
//                arrayList2.add(next)
//            }
//        }
//        val dutyStatusNameFromLogDisplaySettings: List<String> =
//            getDutyStatusNameFromLogDisplaySettings(arrayList2 as List<*>)
//        val arrayList3: MutableCollection<*> = ArrayList<Any?>()
//        for (next2 in lastDutyLogFromMilli) {
//            if (!dutyStatusNameFromLogDisplaySettings.contains(next2.getEventName())) {
//                arrayList3.add(next2)
//            }
//        }
//        val list3 = arrayList3 as List<*>
//        val arrayList4: MutableCollection<*> = ArrayList<Any?>()
//        for (next3 in logDayEventLogs) {
//            if (!dutyStatusNameFromLogDisplaySettings.contains(next3.getEventName())) {
//                arrayList4.add(next3)
//            }
//        }
//        val list4: List<EventLog> = arrayList4 as List<*>
//        if (!list3.isEmpty()) {
//            val id: Long = (CollectionsKt.first(lastDutyLogFromMilli) as EventLog).getId()
//            val longValue = id ?: -1
//            val serverId: String =
//                (CollectionsKt.first(lastDutyLogFromMilli) as EventLog).getServerId()
//            str4 = serverId ?: ""
//            val trimEventStatus: String =
//                trimEventStatus((CollectionsKt.first(lastDutyLogFromMilli) as EventLog).getEventName())
//            if (Intrinsics.areEqual(
//                    (CollectionsKt.first(lastDutyLogFromMilli) as EventLog).getLocationDescription() as Any?,
//                    "E" as Any
//                )
//            ) {
//                str5 = "Location Not Available"
//            } else {
//                str5 =
//                    (CollectionsKt.first(lastDutyLogFromMilli) as EventLog).getLocationDescription()
//            }
//            arrayList.add(
//                LogData(
//                    longValue,
//                    str4,
//                    "00:00",
//                    trimEventStatus,
//                    str5,
//                    java.lang.String.valueOf((CollectionsKt.first(lastDutyLogFromMilli) as EventLog).getEngineMiles()),
//                    java.lang.String.valueOf((CollectionsKt.first(lastDutyLogFromMilli) as EventLog).getEngineHours()),
//                    if ((CollectionsKt.first(lastDutyLogFromMilli) as EventLog).isAutoGenerated()) "Auto" else "Manual",
//                    false,
//                    256,
//                    null as DefaultConstructorMarker?
//                )
//            )
//        }
//        for (eventLog in list4) {
//            val id2: Long = eventLog.getId()
//            val longValue2 = id2 ?: -1
//            val serverId2: String = eventLog.getServerId()
//            str2 = serverId2 ?: ""
//            val milliToDate: String =
//                DateUtils.INSTANCE.milliToDate(eventLog.getEventUTCTimestamp())
//            val trimEventStatus2: String = trimEventStatus(eventLog.getEventName())
//            if (Intrinsics.areEqual(eventLog.getLocationDescription() as Any?, "E" as Any)) {
//                str3 = "Location Not Available"
//            } else {
//                str3 = eventLog.getLocationDescription()
//            }
//            arrayList.add(
//                LogData(
//                    longValue2,
//                    str2,
//                    milliToDate,
//                    trimEventStatus2,
//                    str3,
//                    java.lang.String.valueOf(eventLog.getEngineMiles()),
//                    java.lang.String.valueOf(eventLog.getEngineHours()),
//                    if (eventLog.isAutoGenerated()) "Auto" else "Manual",
//                    false,
//                    256,
//                    null as DefaultConstructorMarker?
//                )
//            )
//        }
//        return arrayList
//    }

}