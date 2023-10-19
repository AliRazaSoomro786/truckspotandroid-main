package com.truckspot.repository

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.truckspot.api.TruckSpotAPI
import com.truckspot.models.LoginResponse
import com.truckspot.request.AddLogRequest
import com.truckspot.request.LoginRequest
import com.truckspot.utils.Constants.TAG
import com.truckspot.utils.NetworkResult
import com.truckspot.utils.PrefRepository
import retrofit2.Response
import java.math.BigDecimal
import javax.inject.Inject
var miles: BigDecimal? = null
var mOdometer: String? = null
var mEngineHours: String? = null
var speed: Int = 0
var location: String? = null

class LoginRespository @Inject constructor(private val truckSpotAPI: TruckSpotAPI) {

    lateinit var prefRepository: PrefRepository
    var mActivity: FragmentActivity? = null

    private val _loginResponseLiveData = MutableLiveData<NetworkResult<LoginResponse>>()
    val loginResponseLiveData: LiveData<NetworkResult<LoginResponse>>
        get() = _loginResponseLiveData

    suspend fun loginUser(loginRequest: LoginRequest) {
        _loginResponseLiveData.postValue(NetworkResult.Loading())
        val response = truckSpotAPI.login(loginRequest)

        Log.d("check here",response.body().toString())
        handleResponse(response)

        if (response.isSuccessful && response.body() != null) {
            _loginResponseLiveData.postValue(NetworkResult.Success(response.body()!!))

            // The user is successfully logged in, so you can call addLog API here
             val logRequest = AddLogRequest(
                "off", "99999", "999999", 2, "loc not available", 5,"0".toInt(),"0".toInt(),"1C6RREHT5NN451094"
            )
            try {
                val addLogResponse = truckSpotAPI.addLog(logRequest)
                // Handle the response of the addLog API as needed
                if (addLogResponse.isSuccessful && addLogResponse.body() != null) {
                    Log.d("check type here", "Add Log API Response: ${addLogResponse.body()?.toString()}")
                } else {
                    Log.e(TAG, "Add Log API Error: ${addLogResponse.code()} - ${addLogResponse.message()}")
                }
            } catch (e: Exception) {
                Log.e("ssss", "Error calling Add Log API: ${e.message}")
            }


        } else if (response.errorBody() != null) {
            _loginResponseLiveData.postValue(NetworkResult.Error("Something Went wrong at API END"))
        } else {
            _loginResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
}
private suspend fun handleResponse(response: Response<LoginResponse>) {
        if (response.isSuccessful && response.body() != null) {
            _loginResponseLiveData.postValue(NetworkResult.Success(response.body()!!))


            // adding user logs record here & state information as well


        }

                else if (response.errorBody() != null) {
                //val errorObj = JSONObject(response.errorBody()?.charStream()?.readText())
                _loginResponseLiveData.postValue(NetworkResult.Error("Something Went wrong at API END"))
            } else {
                _loginResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
            }
            }
        }

