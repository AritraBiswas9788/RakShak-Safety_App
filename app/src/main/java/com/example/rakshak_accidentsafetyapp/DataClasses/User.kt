package com.example.rakshak_accidentsafetyapp.DataClasses

import com.example.rakshak_accidentsafetyapp.DataClasses.Contact
import com.example.rakshak_accidentsafetyapp.DataClasses.Location

data class User(
    var name:String="",
    val email:String="",
    var image:String="",
    val uid:String="",
    val fcmtoken:String="",
    val location: Location,
    val trackList: ArrayList<String> = arrayListOf(),
    val contactList: ArrayList<Contact> = arrayListOf()

)
