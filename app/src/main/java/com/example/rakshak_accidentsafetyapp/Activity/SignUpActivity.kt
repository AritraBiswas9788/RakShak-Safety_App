package com.example.rakshak_accidentsafetyapp.Activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.rakshak_accidentsafetyapp.DataClasses.GeoFireInfo
import com.example.rakshak_accidentsafetyapp.DataClasses.Location
import com.example.rakshak_accidentsafetyapp.R
import com.example.rakshak_accidentsafetyapp.DataClasses.User
import com.example.rakshak_accidentsafetyapp.Notification.MyFirebaseMessagingService
import com.example.rakshak_accidentsafetyapp.Notification.MyFirebaseMessagingService.Companion.token
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.util.UUID


class SignUpActivity : AppCompatActivity() {
   val TOPIC1= "topics/myTopic"
    private lateinit var mdatabaseref: DatabaseReference
    private lateinit var mauth: FirebaseAuth
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var button: Button
    private lateinit var address: EditText
    private lateinit var imageView: ImageView
    private val PICK_IMAGE_REQUEST = 1
    private var imageUrl: String? = null
    private lateinit var uriImg: String
    private lateinit var ref:DatabaseReference

    private var storageRef: StorageReference = Firebase.storage.reference
    private var dbRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var currentLocation: Location

    val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            Glide.with(this)
                .load(uri)

                .centerCrop()
                .circleCrop()
                .into(imageView)
            uriImg = uri.toString()
            Log.i("uriCheck", uri.toString())


        }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        ActivityCompat.requestPermissions(this,
            (arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.CAMERA)),
            PackageManager.PERMISSION_GRANTED
        )


        name = findViewById(R.id.editTextname)
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        button = findViewById(R.id.register)
        mauth = FirebaseAuth.getInstance()
        address = findViewById(R.id.editTextAddress)
        mdatabaseref = FirebaseDatabase.getInstance().getReference("Users")
        imageView = findViewById(R.id.uploadPic)
         ref=FirebaseDatabase.getInstance().getReference("Users")


          imageView.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        button.setOnClickListener {
            registeruser()
        }

            FirebaseMessaging.getInstance().token.addOnSuccessListener{
                MyFirebaseMessagingService.token =it.toString()
            }

        FirebaseMessaging.getInstance().subscribeToTopic(Topic1)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun registeruser() {

        val name1 = name.text.toString().trim { it <= ' ' }
        val email1 = email.text.toString().trim { it <= ' ' }
        val addr = address.text.toString().trim()
        val password = password.text.toString().trim() { it <= ' ' }
        if (validateForm(name1, email1, password)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                email1, password
            ).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val firebaseuser: FirebaseUser = task.result!!.user!!
                    val firebaseemail = firebaseuser.email

                    val sharedPreferences = getSharedPreferences("TokenPreferences", MODE_PRIVATE)
                    val savedToken = sharedPreferences.getString("SavedToken", "Nothing")
                    if (savedToken.equals("Nothing")) {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.w(
                                        "Task Unsuccessful",
                                        "Fetching FCM registration token failed",
                                        task.exception
                                    )
                                    return@OnCompleteListener
                                }
                                // Get new FCM registration token
                                val token = task.result.toString()
                                 FirebaseMessaging.getInstance().subscribeToTopic(TOPIC1)
                                val fileName = UUID.randomUUID().toString()
                                val ImagesRef =
                                    storageRef.child("images/$fileName.jpg")
                                val baos = ByteArrayOutputStream()
                                val source = ImageDecoder.createSource(contentResolver, Uri.parse(uriImg))
                                val bitmap = ImageDecoder.decodeBitmap(source)
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                val data = baos.toByteArray()
                                val uploadTask = ImagesRef.putBytes(data)
                                uploadTask.addOnFailureListener {
                                    Log.i("uploadCheck", "Failed")
                                }
                                    .addOnSuccessListener { taskSnapshot ->

                                        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                                        if (ActivityCompat.checkSelfPermission(
                                                this,
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                                this,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            ) != PackageManager.PERMISSION_GRANTED
                                        ) {
                                            Log.e("mapCheck", "Permission-failed")
                                        }
                                        val locationListener = LocationListener { location ->
                                            currentLocation =
                                                Location(location.latitude, location.longitude)

                                            storageRef.child("images/$fileName.jpg").downloadUrl.addOnSuccessListener { url ->
                                                val user = User( name = name1, email = email1, image = url.toString(), uid = firebaseuser.uid, fcmtoken = token, location = currentLocation, trackList = arrayListOf(), contactList = arrayListOf(), geoFireInfo = GeoFireInfo())

                                                mdatabaseref.child("${firebaseuser.uid}").setValue(user)
                                                    .addOnCompleteListener {

                                                        val geoFire=GeoFire(ref.child(mauth.currentUser!!.uid))

                                                        geoFire.setLocation(
                                                            "geoFireInfo",
                                                            GeoLocation(location.latitude, location.longitude),
                                                            object : GeoFire.CompletionListener {
                                                                override fun onComplete(key: String?, error: DatabaseError?) {
                                                                    if(error==null)
                                                                    {
                                                                        Log.i("geofire","location stored successfully")
                                                                    }
                                                                    else
                                                                    {
                                                                        Log.i("geofire","error in geofire ")
                                                                    }
                                                                }

                                                            })
                                                        Log.d("Your Device Token=>>>", token)
                                                        val editPref: SharedPreferences.Editor = sharedPreferences.edit()
                                                        editPref.putString("SavedToken", token)
                                                        editPref.commit()

                                                        finishAffinity()
                                                        val intent: Intent = Intent(this, MainActivity::class.java)
                                                        startActivity(intent)

                                                    }
                                                    .addOnFailureListener {
                                                        Log.e("dbCheck",it.toString())
                                                    }



                                            }

                                        }
                                        locationManager.requestLocationUpdates(
                                            LocationManager.NETWORK_PROVIDER,
                                            500,
                                            0f,
                                            locationListener
                                        )


                                    }



                            })
                    }
                } else {
                    Toast.makeText(
                        this,
                        "please check your connection and enter the correct credential",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
        }


    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                errorSnackBar("Please Enter your Name")
                false
            }

            TextUtils.isEmpty(email) -> {
                errorSnackBar("Please Enter your Email")
                false
            }

            TextUtils.isEmpty(password) -> {
                errorSnackBar("Please Enter your Password")
                false
            }

            else -> {
                true
            }
        }

    }

    fun errorSnackBar(message: String) {
        val snackbar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
                this,
                com.jpardogo.android.googleprogressbar.library.R.color.red
            )
        )
        snackbar.show()

    }


}