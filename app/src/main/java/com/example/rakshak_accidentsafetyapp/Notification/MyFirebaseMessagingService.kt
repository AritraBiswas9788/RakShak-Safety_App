package com.example.rakshak_accidentsafetyapp.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.rakshak_accidentsafetyapp.Activity.MainActivity
import com.example.rakshak_accidentsafetyapp.Activity.TurnByTurnExperienceActivity
import com.example.rakshak_accidentsafetyapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val tags= "AdityaKumar"
private const val channelid:String= "MyNameIsAdityaKumar"
class MyFirebaseMessagingService: FirebaseMessagingService() {

    companion object
    {
        var sharedPref: SharedPreferences?=null
        var token: String?
            get()  {
                return sharedPref?.getString("token","")
            }
            set(value)
            {
                sharedPref?.edit()?.putString("token",value)?.apply()
            }
    }

    override fun onNewToken(newtoken: String) {
        super.onNewToken(newtoken)
        token=newtoken
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val lat:Double = message.data["lat"]!!.toDouble()
        val long:Double = message.data["long"]!!.toDouble()


        val intent= Intent(this,TurnByTurnExperienceActivity::class.java)
        intent.putExtra("lat",lat)
        intent.putExtra("long",long)
        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationid = Random.nextInt()

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O)
        {
            createNotificationChannel(notificationManager)
        }

        val pendingintent = PendingIntent.getActivity(this,0,intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, channelid)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.baseline_notification_message_24)
            .setAutoCancel(true)
            .setContentIntent(pendingintent)
            .build()

        notificationManager.notify(notificationid,notification)


    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationmanager: NotificationManager)
    {
        val channelName="channelName"
        val channel = NotificationChannel(
            channelid,channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description= "My channel description"
            enableLights(true)
            lightColor = Color.GREEN

        }
        notificationmanager.createNotificationChannel(channel)


    }

//
//    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
//if(message.notification!=null)
//{
//    var title=message.notification!!.title
//    var body=message.notification!!.body
//    var data=Gson().toJson(message.data)
//
//    // create notification to show
//    ShowNotification(this,title!!,body!!)
//    Log.d(tags,data)
//}
//        else{
//            Log.e("error","error in notify")
//        }
//
//
//    }

}
//fun ShowNotification(context: android.content.Context, title: String, body: String) {
//
//    val notification = NotificationCompat.Builder(context, channelid)
//        .setContentTitle(title)
//        .setContentText(body)
//        .setSmallIcon(R.drawable.baseline_notification_message_24).setPriority(
//            NotificationCompat.PRIORITY_MAX
//        )
//    // style
//    var bigtextstyle = NotificationCompat.BigTextStyle()
//    bigtextstyle.bigText(title)
//    bigtextstyle.setBigContentTitle(title)
//    bigtextstyle.setSummaryText(title)
//    notification.setStyle(bigtextstyle)
//
//    var manager =
//        context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as NotificationManager
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        var channel1 = "MyNameIsAdityaKumar.id"
//        val channel = NotificationChannel(
//            channel1,
//            "AdityaKumarChannel",
//            NotificationManager.IMPORTANCE_HIGH
//        )
//        manager.createNotificationChannel(channel)
//        notification.setChannelId(channel1)
//    }
//    manager.notify(Random.nextInt(), notification.build())
//
//
//}