package com.truckspot.utils
import com.truckspot.api.TruckSpotAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val Base_URL = "https://dev-api.truckspoteld.com/"
    private val retrofit by lazy {

        Retrofit.Builder().baseUrl(Base_URL).addConverterFactory(GsonConverterFactory.create())
            .build()

    }
    val apiInterface by lazy {
        retrofit.create(TruckSpotAPI::class.java)
    }
}
