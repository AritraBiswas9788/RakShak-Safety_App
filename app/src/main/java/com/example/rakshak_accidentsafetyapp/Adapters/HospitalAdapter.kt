package com.example.rakshak_accidentsafetyapp.Adapters

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.Voice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.rakshak_accidentsafetyapp.Activity.LocationPointActivity
import com.example.rakshak_accidentsafetyapp.Activity.TurnByTurnExperienceActivity
import com.example.rakshak_accidentsafetyapp.DataClasses.TrackItem
import com.example.rakshak_accidentsafetyapp.R
import com.mapbox.search.discover.DiscoverResult
import java.util.Locale


class HospitalAdapter(val context: Context, var list: ArrayList<DiscoverResult>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var itemList = arrayListOf<DiscoverResult>()
    init {
        itemList.clear()
        itemList.addAll(list)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.hospitalitem,parent,false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hosp = itemList[position]
        val viewholder: ItemViewHolder = holder as ItemViewHolder

        viewholder.name.text=hosp.name
        viewholder.lat.text=hosp.coordinate.latitude().toString()
        viewholder.long.text=hosp.coordinate.longitude().toString()
        viewholder.name.setOnClickListener {
            val intent=Intent(context, TurnByTurnExperienceActivity::class.java)
            intent.putExtra("lat",hosp.coordinate.latitude())
            intent.putExtra("long",hosp.coordinate.longitude())
            context.startActivity(intent)
        }

    }


    fun updateList(newList:ArrayList<DiscoverResult>)
    {
        itemList.clear()
        itemList.addAll(newList)
        Log.i("busCheck", itemList.size.toString())
        notifyDataSetChanged()
    }


    class ItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var name = itemView.findViewById<TextView>(R.id.name)
        var lat = itemView.findViewById<TextView>(R.id.latitude)
        var long = itemView.findViewById<TextView>(R.id.longitude)


    }
}