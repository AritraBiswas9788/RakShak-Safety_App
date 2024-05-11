package com.example.rakshak_accidentsafetyapp.Fragments

import android.R.attr.label
import android.R.attr.text
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rakshak_accidentsafetyapp.Adapters.TrackAdapter
import com.example.rakshak_accidentsafetyapp.DataClasses.TrackItem
import com.example.rakshak_accidentsafetyapp.DataClasses.User
import com.example.rakshak_accidentsafetyapp.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DashBoardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var toggleService: SwitchMaterial
    private lateinit var addPersonButton: AppCompatButton
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var trackAdapter:TrackAdapter

    private lateinit var copy: ImageView
    private lateinit var token: TextView
    private var dbRef = FirebaseDatabase.getInstance().getReference("Users")
    private var mauth= FirebaseAuth.getInstance()
    private var trackList = arrayListOf<TrackItem>()
    private var userData = User()

    init {
        val uid = mauth.currentUser!!.uid
        dbRef.child(uid).get().addOnSuccessListener { snap ->
            val user = snap.getValue(User::class.java)
            if (user != null) {
                userData=user
            }
            Log.i("dbcheck",user.toString())
            trackList.clear()
            trackList.addAll(user!!.trackList)
            trackAdapter?.updateList(trackList)
            Log.i("busCheck",trackList.size.toString())
            token.text=user.uid


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
        return inflater.inflate(R.layout.fragment_dash_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackAdapter= TrackAdapter(requireContext(),trackList)
        toggleService = view.findViewById<SwitchMaterial>(R.id.toggleService)
        addPersonButton = view.findViewById(R.id.addPerson)
        copy = view.findViewById(R.id.copy)
        token = view.findViewById(R.id.tokenText)

        copy.setOnClickListener {
            val clipboard: ClipboardManager? =
                context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("copy-text", token.text.toString())
            clipboard?.setPrimaryClip(clip)
        }
        addPersonButton.setOnClickListener {
            addPersonDialog()
        }
        toggleService.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableService()
            }
        }

        trackRecyclerView = view.findViewById(R.id.trackList)
        trackRecyclerView.adapter=trackAdapter
        trackRecyclerView.layoutManager=LinearLayoutManager(context)
        trackAdapter.updateList(trackList)
    }

    private fun addPersonDialog() {
        val builder = AlertDialog.Builder(
            requireContext()
        )

        val viewInflated: View = LayoutInflater.from(context)
            .inflate(R.layout.add_person_dialog, view as ViewGroup?, false)
        val name = viewInflated.findViewById<View>(R.id.inputName) as EditText
        val token = viewInflated.findViewById<View>(R.id.inputToken) as EditText

        builder.setView(viewInflated)
        builder.setTitle("ADD PERSON TO TRACK")

        builder.setPositiveButton(
            "OK"
        ) { dialog, which ->

            checkData(name.text.toString(),token.text.toString())
            dialog.dismiss()

        }
        builder.setNegativeButton(
            "CANCEL"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun checkData(name: String, token: String) {

        val uid = mauth.currentUser!!.uid
        dbRef.child(token).get().addOnSuccessListener {snap ->
            val user = snap.getValue(User::class.java)

            if(user==null)
            {
                Toast.makeText(context,"No Such Token found",Toast.LENGTH_SHORT).show()
            }
            else
            {
                trackList.add(TrackItem(name,token,user.image))
                trackAdapter.updateList(trackList)
                userData.trackList.clear()
                userData.trackList.addAll(trackList)
                dbRef.child(uid).setValue(userData)
                    .addOnCompleteListener {
                        Log.i("dbCheck","track-list updated")
                    }

            }
        }
    }

    private fun enableService() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setMessage("This creates a back-ground service that constantly runs in the background and checks the phone's accelerometer and GPS readings to detect a crash in real-time. Do you want to enable this setting?")
        builder.setTitle("DISCLAIMER")
        builder.apply {
            setPositiveButton("YES") { _, _ ->
                showToast("Redirects to accessibility")
                //createAlertDialog("Navigate To the Installed apps section and enable My WhatsApp Accessibility for WhatsApp Automation to work.","STEPS for Accessibility")
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setMessage("Navigate To the Installed apps section and enable Accident Service for Automatic Accident Detection to work.")
                builder.setTitle("STEPS for Accessibility")
                builder.apply {
                    setPositiveButton("OK") { dialog, id ->
                        val intentAccess = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        intentAccess.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intentAccess)

                        /*val intent = Intent(context, CheckerActivity::class.java)
                        context.startActivity(intent)*/
                    }
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()

            }
            setNegativeButton("CANCEL") { _: DialogInterface, _: Int ->
                toggleService.isChecked = false
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashBoardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}