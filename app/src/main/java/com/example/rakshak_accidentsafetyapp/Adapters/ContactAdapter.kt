package com.example.rakshak_accidentsafetyapp.Adapters

import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.rakshak_accidentsafetyapp.DataClasses.Contact
import com.example.rakshak_accidentsafetyapp.DataClasses.User
import com.example.rakshak_accidentsafetyapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ContactAdapter(val context: Context, var list: ArrayList<Contact>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dbRef = FirebaseDatabase.getInstance().getReference("Users")
    private var mauth = FirebaseAuth.getInstance()
    var itemList = arrayListOf<Contact>()

    init {
        itemList.clear()
        itemList.addAll(list)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.contactitem, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mem = itemList[position]
        val viewholder: ItemViewHolder = holder as ItemViewHolder
        viewholder.name.text = mem.name
        viewholder.number.text = mem.phno
        viewholder.smsBtn.setOnClickListener {
            createAlertDialog(mem.phno)
        }

    }

    private fun createAlertDialog(phno: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("This will send an SOS SMS signal to your selected contact with your current Location co-ordinates. Are you sure you want to proceed?")
        builder.setTitle("DISCLAIMER")
        builder.apply {
            setPositiveButton("YES") { _, _ ->
                sendSMSsignal(phno)
            }
            setNegativeButton("CANCEL") { _, _ ->

            }
        }.create().show()


    }

    private fun sendSMSsignal(phno: String) {
        var message =
            "Hello! I am currently in danger at "
        val uid = mauth.currentUser!!.uid
        dbRef.child(uid).get().addOnSuccessListener { snap ->
            val user = snap.getValue(User::class.java)
            message += "latitude ${user?.geoFireInfo?.l?.get(1)} and longitude ${
                user?.geoFireInfo?.l?.get(
                    0
                )
            }. Please send help."

            Log.i("smscheck",message)

            val mysmsmanager = context.getSystemService(SmsManager::class.java)
            mysmsmanager.sendTextMessage(phno, null, message, null, null)
        }.addOnFailureListener {
            Log.e("smscheck",it.toString())
        }
    }


    fun updateList(newList: ArrayList<Contact>) {
        itemList.clear()
        itemList.addAll(newList)
        Log.i("busCheck", itemList.size.toString())
        notifyDataSetChanged()
    }
}


class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var name: TextView = itemView.findViewById(R.id.name)
    var number: TextView = itemView.findViewById(R.id.number)
    var smsBtn: ImageView = itemView.findViewById(R.id.sms)

}

