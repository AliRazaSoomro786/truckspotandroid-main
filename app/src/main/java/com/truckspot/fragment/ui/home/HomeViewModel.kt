package com.truckspot.fragment.ui.home

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.truckspot.api.TruckSpotAPI
import com.truckspot.models.DRIVE_MODE
import com.truckspot.models.GetLogsResponse
import com.truckspot.models.LogResponse
import com.truckspot.repository.DashboardRepository
import com.truckspot.request.AddLogRequest
import com.truckspot.request.AddLogRequestunauth
import com.truckspot.request.updateLogRequest
import com.truckspot.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(var dashboardRespository: DashboardRepository):ViewModel() {
    val logResponseLiveData: LiveData<NetworkResult<LogResponse>>
        get() = dashboardRespository.logResponseLiveData
    var trackingMode: ObservableField<DRIVE_MODE> = ObservableField()
     val getLogsLiveData: LiveData<NetworkResult<GetLogsResponse>>
        get() = dashboardRespository.getLogsLiveData

    fun logUser (logRequest: AddLogRequest){
//        Log.v("hello from add log")
        Log.d("checkdat","latest data : ${logRequest}")
         viewModelScope.launch {
            dashboardRespository.addLog(logRequest)
          }
    }

    fun logUserunauth (logRequest:AddLogRequestunauth){
         viewModelScope.launch {
            dashboardRespository.addLogUnauth(logRequest)
        }
    }
    @SuppressLint("LongLogTag")
    fun updateLog (updateLogReq: updateLogRequest){
        Log.d("updateapibeingimplemented", updateLogReq.toString())

        viewModelScope.launch {
            dashboardRespository.updateLog(updateLogReq)

        }
    }

    fun getLogs(page: Int, pageSize: Int, days: Int) {
        viewModelScope.launch {
            dashboardRespository.getLogs(page, pageSize, days)

        }
    }

    suspend fun getCustomLogs(page: Int, pageSize: Int, days: Int): Response<GetLogsResponse> {
        return dashboardRespository.get7DayLogs(page, pageSize, days)
    }
}

fun validateCredentials(
    emailAddress: String, userName: String, password: String, isLogin: Boolean
): Pair<Boolean, String> {

    var result = Pair(true, "")
    if (TextUtils.isEmpty(emailAddress) || (!isLogin && TextUtils.isEmpty(userName)) || TextUtils.isEmpty(
            password
        )
    ) {
        result = Pair(false, "Please provide the credentials")
    }
//        else if(!Helper.isValidEmail(emailAddress)){
//            result = Pair(false, "Email is invalid")
//        }
    else if (!TextUtils.isEmpty(password) && password.length <= 2) {
        result = Pair(false, "Password length should be greater than 3")
    }
    return result
}