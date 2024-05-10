package com.example.rakshak_accidentsafetyapp.Fragments

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.rakshak_accidentsafetyapp.R
import com.google.android.material.switchmaterial.SwitchMaterial


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashBoardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashBoardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var toggleService: SwitchMaterial
    private lateinit var addPersonButton: AppCompatButton

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

        toggleService = view.findViewById<SwitchMaterial>(R.id.toggleService)
        addPersonButton = view.findViewById(R.id.addPerson)
        addPersonButton.setOnClickListener {
            addPersonDialog()
        }
        toggleService.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                enableService()
            }
        }
    }

    private fun addPersonDialog() {
        val builder = AlertDialog.Builder(
            requireContext()
        )

        val viewInflated: View = LayoutInflater.from(context)
            .inflate(R.layout.add_person_dialog, view as ViewGroup?, false)
// Set up the input
// Set up the input
        val name = viewInflated.findViewById<View>(com.example.rakshak_accidentsafetyapp.R.id.inputName) as EditText
        val token = viewInflated.findViewById<View>(com.example.rakshak_accidentsafetyapp.R.id.inputToken) as EditText
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated)
        builder.setTitle("ADD PERSON TO TRACK")
// Set up the buttons

// Set up the buttons
        builder.setPositiveButton(
            "OK"
        ) { dialog, which ->
            dialog.dismiss()

        }
        builder.setNegativeButton(
            "CANCEL"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
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