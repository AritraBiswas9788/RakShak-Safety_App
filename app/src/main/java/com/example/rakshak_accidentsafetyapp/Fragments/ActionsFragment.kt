package com.example.rakshak_accidentsafetyapp.Fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rakshak_accidentsafetyapp.Activity.TurnByTurnExperienceActivity
import com.example.rakshak_accidentsafetyapp.Adapters.ContactAdapter
import com.example.rakshak_accidentsafetyapp.Adapters.HospitalAdapter
import com.example.rakshak_accidentsafetyapp.DataClasses.Contact
import com.example.rakshak_accidentsafetyapp.DataClasses.User
import com.example.rakshak_accidentsafetyapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.search.discover.Discover
import com.mapbox.search.discover.DiscoverQuery
import com.mapbox.search.discover.DiscoverResult


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ActionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var contactList: RecyclerView
    private lateinit var contactAdapter:ContactAdapter
    private lateinit var contactBtn:ImageView
    private lateinit var empty:ImageView
    private lateinit var hospbtn:ImageView

    private lateinit var hospitalRecView:RecyclerView
    var numList = arrayListOf<Contact>()

    private var dbRef = FirebaseDatabase.getInstance().getReference("Users")
    private var mauth= FirebaseAuth.getInstance()
    private var userData = User()

    private lateinit var currentLocation: Point

    init {
            val uid = mauth.currentUser!!.uid
            dbRef.child(uid).get().addOnSuccessListener { snap ->
                val user = snap.getValue(User::class.java)
                if (user != null) {
                    userData = user
                }
            Log.i("dbcheck", user.toString())
            numList.clear()
            numList.addAll(user!!.contactList)
            contactAdapter?.updateList(numList)
            Log.i("busCheck", numList.size.toString())
            if(numList.isEmpty()) {
                empty.visibility = View.VISIBLE
                contactList.visibility=View.GONE
            }
            else
            {
                empty.visibility =View.GONE
                contactList.visibility= View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hospitalRecView=view.findViewById(R.id.hospitalList)
        contactList=view.findViewById(R.id.contactList)
        contactBtn=view.findViewById(R.id.contactbtn)
        hospbtn=view.findViewById(R.id.hospbtn)
        contactAdapter= ContactAdapter(requireContext(),numList)
        contactList.adapter=contactAdapter
        contactList.layoutManager=LinearLayoutManager(context)
        empty=view.findViewById(R.id.empty)
        hospbtn.setOnClickListener {
            val intent=Intent(requireContext(),TurnByTurnExperienceActivity::class.java)

            requireContext().startActivity(intent)
        }
        getNearbyPOIs()
        if(numList.isEmpty()) {
            empty.visibility = View.VISIBLE
            contactList.visibility=View.GONE
        }
        else
        {
            empty.visibility =View.GONE
            contactList.visibility= View.VISIBLE
        }
        contactBtn.setOnClickListener {
            val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(pickContact,1);
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        if (requestCode == 1 && data != null) {
            val contactUri = data.data
            val cursor: Cursor = context?.contentResolver!!.query(contactUri!!, null, null, null, null)!!
            cursor.moveToFirst()
            val phoneIndex: Int =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val nameIndex: Int =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneNo = cursor.getString(phoneIndex)
            val name = cursor.getString(nameIndex)
            Log.e("datacheck", "$name,$phoneNo")
            cursor.close()

            updateData(Contact(name,phoneNo))
        }
    }

    private fun updateData(contact: Contact) {

        val uid = mauth.currentUser!!.uid
        numList.add(contact)
        if(numList.isEmpty()) {
            empty.visibility = View.VISIBLE
            contactList.visibility=View.GONE
        }
        else
        {
            empty.visibility =View.GONE
            contactList.visibility= View.VISIBLE
        }
        userData.contactList.clear()
        userData.contactList.addAll(numList)
        contactAdapter.updateList(numList)
        dbRef.child(uid).setValue(userData)
            .addOnCompleteListener {
                Log.i("dbCheck","contact-list updated")
            }


    }
    private fun getNearbyPOIs() {
        val discover =
            Discover.create("pk.eyJ1IjoiYXJpdHJhOTc4OCIsImEiOiJjbG16MXVkM3IxY3k2MndudnB1eWp6cm01In0.0a62xnPfsD67MQYJieU5og")
        val query = DiscoverQuery.Category.HOSPITAL
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
                    val retList:ArrayList<DiscoverResult> = arrayListOf()
                    retList.addAll(results)
                    setUpRecyclerView(retList)

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


    }

    private fun setUpRecyclerView(locationList: ArrayList<DiscoverResult>) {

        val hospitalAdapter = HospitalAdapter(requireContext(),locationList)
        hospitalRecView.adapter=hospitalAdapter
        hospitalRecView.layoutManager=LinearLayoutManager(context)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ActionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ActionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}