package com.example.rakshak_accidentsafetyapp.Activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.example.rakshak_accidentsafetyapp.DataClasses.User
import com.example.rakshak_accidentsafetyapp.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.search.discover.DiscoverResult

class   LocationPointActivity : AppCompatActivity() {
    var PERMISSION_ALL = 1
    var PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    private lateinit var mapView: MapView
    private lateinit var reBtn: ExtendedFloatingActionButton
    private lateinit var currentLocation: Point
    private var dbRef = FirebaseDatabase.getInstance().getReference("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uid = intent.getStringExtra("uid")!!
        var mauth= FirebaseAuth.getInstance()
        setContentView(R.layout.activity_location_point)
        mapView = findViewById(R.id.map)
        reBtn = findViewById(R.id.recenter)
        /*val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = LocationListener { location ->
            currentLocation= location.latitude
            long = location.longitude

        }*/
        //mapView= MapView(this)

        reBtn.setOnClickListener {
            reCenterView(uid)
        }

        dbRef.child(uid).get().addOnSuccessListener { snap ->
            val user = snap.getValue(User::class.java)!!

            onMapReady(Point.fromLngLat(user.geoFireInfo.l[1],user.geoFireInfo.l[0]))
        }

    }

    private fun reCenterView(uid:String) {
        dbRef.child(uid).get().addOnSuccessListener { snap ->
            val user = snap.getValue(User::class.java)!!
            onMapReady(Point.fromLngLat(user.geoFireInfo.l[1],user.geoFireInfo.l[0]))
        }
    }

    private fun dropMarker(point: Point) {

        bitmapFromDrawableRes(
            this,
            R.drawable.marker
        )?.let {
            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            val pointAnnotationOptions: PointAnnotationOptions =
                PointAnnotationOptions().withIconImage(it)
                    .withPoint(point)
            pointAnnotationManager.create(pointAnnotationOptions)
        }

    }

    private fun onMapReady(point: Point) {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(point)
                .zoom(14.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            //initLocationComponent()
            setupGesturesListener()
        }
        //getNearbyPOIs()
        dropMarker(point)


    }
    /*private fun getNearbyPOIs() {
        val discover =
            Discover.create("pk.eyJ1IjoiYXJpdHJhOTc4OCIsImEiOiJjbG16MXVkM3IxY3k2MndudnB1eWp6cm01In0.0a62xnPfsD67MQYJieU5og")
        val query = DiscoverQuery.Category.HOSPITAL
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
            return
        }

        val locationListener = LocationListener { location ->
            currentLocation = Point.fromLngLat(location.longitude, location.latitude)

            Log.i(
                "mapCheck",
                "location retrieved ${currentLocation.longitude()} + ${currentLocation.latitude()}"
            )
            lifecycleScope.launchWhenStarted {
                Log.i(
                    "mapCheck",
                    "location used ${currentLocation.longitude()} + ${currentLocation.latitude()}"
                )
                val response = discover.search(
                    query = query,
                    proximity = currentLocation,
                )
                response.onValue { results ->
                    Log.i("mapCheck", results.toString())

                    drawResults(results)
                }.onError { e ->
                    Log.e("mapCheck", "Error happened during search request", e)

                }
            }


        }
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            500,
            0f,
            locationListener
        )
        Log.i("mapCheck", "All done")


    }*/

    private fun drawResults(locationList: List<DiscoverResult>) {

        for ((index, location) in locationList.withIndex()) {

            bitmapFromDrawableRes(
                this,
                R.drawable.hospital
            )?.let {
                val annotationApi = mapView.annotations
                val pointAnnotationManager = annotationApi.createPointAnnotationManager()
                val pointAnnotationOptions: PointAnnotationOptions =
                    PointAnnotationOptions().withIconImage(it)
                        .withPoint(Point.fromLngLat(location.coordinate.longitude(), location.coordinate.latitude()))
                pointAnnotationOptions.textField= location.name
                pointAnnotationManager.create(pointAnnotationOptions)
            }
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                20, 20,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }


    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@LocationPointActivity,
                    R.drawable.circleicon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@LocationPointActivity,
                    R.drawable.circleicon,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        locationComponentPlugin.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }
    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }
}
