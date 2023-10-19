package com.truckspot.di

import android.content.Context
import com.truckspot.BuildConfig
import com.truckspot.BuildConfig.API_KEY
import com.truckspot.api.TruckSpotAPI
import com.truckspot.utils.Constants.BASE_URL
import com.truckspot.utils.PrefRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {

        return Retrofit.Builder().client(client)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build()
    }



    @Singleton
    @Provides
    fun provideGeIdeaAPI(retrofit: Retrofit):TruckSpotAPI{
        return  retrofit.create(TruckSpotAPI::class.java)

    }


    @Singleton
    @Provides
    fun provideOkHttpClient(@ApplicationContext context: Context) = if (BuildConfig.DEBUG) {
        val token =PrefRepository(context)
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val requestInterceptor = Interceptor { chain ->
//            val url = chain.request()
//                .url
//                .newBuilder()
//
//                .build()

            val request = chain.request()
                .newBuilder()
                .addHeader("authorization", "Bearer ${token.getToken()}")
                .build()
            return@Interceptor chain.proceed(request)
        }

        OkHttpClient
            .Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(90L, TimeUnit.SECONDS)
            .writeTimeout(90L, TimeUnit.SECONDS)
            .addInterceptor(requestInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient
            .Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(90L, TimeUnit.SECONDS)
            .writeTimeout(90L, TimeUnit.SECONDS)
            .build()
    }




}