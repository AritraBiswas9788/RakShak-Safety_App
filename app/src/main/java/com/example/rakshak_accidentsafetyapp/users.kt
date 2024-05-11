package com.example.rakshak_accidentsafetyapp

import com.google.type.LatLng

data class User(
    var name:String="",
    val email:String="",
    var image:String="",
    val uid:String="",
    val fcmtoken:String="",
    val location:Location,
    val trackList: ArrayList<String> = arrayListOf(),
    val contactList: ArrayList<Contact> = arrayListOf()

)
