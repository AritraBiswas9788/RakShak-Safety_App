package com.example.rakshak_accidentsafetyapp.Notification

import com.example.rakshak_accidentsafetyapp.Notification.Constants.Companion.Content_type
import com.example.rakshak_accidentsafetyapp.Notification.Constants.Companion.Server_key
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {
    @Headers("Authorization: key=$Server_key","Content-Type:$Content_type")
    @POST("fcm/send")
    suspend fun postNotification(

        @Body notification:PushNotification

    ): Response<ResponseBody>
}