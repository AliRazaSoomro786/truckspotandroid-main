package com.truckspot.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.truckspot.api.TruckSpotAPI
import com.truckspot.models.CertifyModelItem
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

class CertifyRepository @Inject constructor(private val truckSpotAPI: TruckSpotAPI) {

    private val certifyLiveData = MutableLiveData<NetworkResult<CertifyModelItem>>()
    val certified: LiveData<NetworkResult<CertifyModelItem>>
        get() = certifyLiveData

    suspend fun CertifiedData() {
        try {
            val result = truckSpotAPI.getDates()
            if (result.isSuccessful) {
                val certifyModelItem = result.body() // Assuming the API response is of type CertifyModelItem
                if (certifyModelItem != null) {
                 } else {
                 }
            } else {
                val errorBody = result.errorBody()
                val errorMessage = errorBody?.string() ?: "Unknown error"
                certifyLiveData.postValue(NetworkResult.Error(errorMessage))
            }
        } catch (e: Exception) {
            certifyLiveData.postValue(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }
}
