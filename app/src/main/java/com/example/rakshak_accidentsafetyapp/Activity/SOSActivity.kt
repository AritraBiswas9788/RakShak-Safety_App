package com.example.rakshak_accidentsafetyapp.Activity

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.rakshak_accidentsafetyapp.DataClasses.Contact
import com.example.rakshak_accidentsafetyapp.DataClasses.User
import com.example.rakshak_accidentsafetyapp.Notification.MyFirebaseMessagingService
import com.example.rakshak_accidentsafetyapp.Notification.Notificationdata
import com.example.rakshak_accidentsafetyapp.Notification.PushNotification
import com.example.rakshak_accidentsafetyapp.Notification.RetrofitInstance
import com.example.rakshak_accidentsafetyapp.R
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryDataEventListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val Topic1="topics/myTopic"

class SOSActivity : AppCompatActivity() {
    private lateinit var ref: DatabaseReference
    private lateinit var mauth: FirebaseAuth
 private lateinit var btn:Button
    private lateinit var dbref:DatabaseReference

    private lateinit var listener:ValueEventListener
    private fun sendNotifications(contactList: ArrayList<Contact>) {

        for(contact in contactList)
        {
            var message =
                "Hello! I am currently in danger at "
            val uid = mauth.currentUser!!.uid
            dbref.child(uid).get().addOnSuccessListener { snap ->
                val user = snap.getValue(User::class.java)
                message += "latitude ${user?.geoFireInfo?.l?.get(1)} and longitude ${
                    user?.geoFireInfo?.l?.get(
                        0
                    )
                }. Please send help."

                Log.i("smscheck",message)

                val mysmsmanager = getSystemService(SmsManager::class.java)
                mysmsmanager.sendTextMessage(contact.phno, null, message, null, null)
            }.addOnFailureListener {
                Log.e("smscheck",it.toString())
            }
        }

    }

    //    private lateinit var currentLocation: com.example.rakshak_accidentsafetyapp.DataClasses.Location
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sosactivity)



        mauth = FirebaseAuth.getInstance()
        ref = FirebaseDatabase.getInstance().getReference("Users")
        dbref=FirebaseDatabase.getInstance().getReference("Users")

    dbref.child(mauth.currentUser!!.uid).get().addOnSuccessListener { snap ->
        val user = snap.getValue(User::class.java)
        if (user != null) {
            sendNotifications(user.contactList)
        }
    }

        //sendnotity()

//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//
//        }
//        FirebaseMessaging.getInstance().token.addOnSuccessListener{
//            MyFirebaseMessagingService.token =it.toString()
//        }
        FirebaseMessaging.getInstance().subscribeToTopic(Topic1)
        sendnotity()

//     val btn:Button=findViewById<Button>(R.id.btn)
////    btn.setOnClickListener {
//         PushNotification(Notificationdata("Accident Happened", "Accident Happened at location lt &lg "),Topic1)
//            .also { it ->
//                sendNotification(it)
//            }
////    }
//    btn.setOnClickListener{
//        PushNotification(Notificationdata("Accident Happened", "Accident Happened at location lt &lg "),Topic1)
//            .also { it ->
//                sendNotification(it)
//            }
//    }


    }
    private fun sendnotity() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        }

        val locationListener = LocationListener { location ->

            var flag =true
            listener = dbref.addValueEventListener( object : ValueEventListener {


                override fun onDataChange(snapshot: DataSnapshot) {
                    if(flag){
                    var i:Long=0;
                    var n:Long=snapshot.childrenCount


                    for(snapshot2 in snapshot.children )
                    {


                        Log.i("dcheck",snapshot2.value.toString())
                        var users:User?=snapshot2.getValue(User::class.java)
                        if(users!=null) {
                            val geoFireInfo = users.geoFireInfo
                            val l = geoFireInfo.l
                            if (l.size != 0) {
                                val lat = l[0]
                                val lg = l[1]
                                val lat1 = location.latitude
                                val lg1 = location.longitude
                                val dist = distance(lat1, lg1, lat, lg)
                                if (dist <= 0.6) {
                                    Log.i("userstoken", users.fcmtoken)
                                    PushNotification(
                                        Notificationdata(
                                            "Accident detected in your location",
                                            "there is an accident occur at this location of latitude${lat} and longitude${lg},  they might need your urgent help",lat.toString(),lg.toString()
                                        ), users.fcmtoken
                                    )
                                        .also { it ->
                                            sendNotification(it)
                                        }
                                }
                            }
                            i++;
                            if (i == n) {

                                return

                            }
                        }

                        }


                        flag=false

                    }

                    removeListener()



                }


                override fun onCancelled(error: DatabaseError) {
                    Log.i("error in geo",error.toString())
                }



            })





        }
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            500,
            0f,
            locationListener
        )
    }

    private fun removeListener() {
        dbref.removeEventListener(listener)
    }

    private fun sendNotification(notification: PushNotification)= CoroutineScope(Dispatchers.IO).launch {
        try {
            val response= RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful)
            {
                Log.d("dwnwd", response.toString())
            }
            else
            {
                Log.e("dwnwd",response.errorBody().toString())
            }
        }catch (e:Exception)
        {
            Log.e(ContentValues.TAG,e.toString())
        }


    }
//    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
//        val theta = lon1 - lon2
//        var dist = (Math.sin(deg2rad(lat1))
//                * Math.sin(deg2rad(lat2))
//                + (Math.cos(deg2rad(lat1))
//                * Math.cos(deg2rad(lat2))
//                * Math.cos(deg2rad(theta))))
//        dist = Math.acos(dist)
//        dist = rad2deg(dist)
//        dist = dist * 60 * 1.1515
//        return dist
//    }
//
//    private fun deg2rad(deg: Double): Double {
//        return deg * Math.PI / 180.0
//    }
//
//    private fun rad2deg(rad: Double): Double {
//        return rad * 180.0 / Math.PI
//    }
    private fun distance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,

    ): Double {
        val theta = lon1 - lon2
        var dist =
            Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(
                deg2rad(lat2)
            ) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515

            dist = dist * 1.609344

        return dist
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/ /*::  This function converts decimal degrees to radians             :*/ /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/ /*::  This function converts radians to decimal degrees             :*/ /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }



}