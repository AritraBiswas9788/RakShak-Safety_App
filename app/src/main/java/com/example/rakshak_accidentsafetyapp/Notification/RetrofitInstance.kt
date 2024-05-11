package com.example.rakshak_accidentsafetyapp.Notification

import com.example.rakshak_accidentsafetyapp.Notification.Constants.Companion.Base_url
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object{
        private val retrofit by lazy {
            Retrofit.Builder().baseUrl(Base_url).addConverterFactory(GsonConverterFactory.create()).build()
        }

        val api: NotificationApi by lazy {
            retrofit.create(NotificationApi::class.java)
        }
    }
}