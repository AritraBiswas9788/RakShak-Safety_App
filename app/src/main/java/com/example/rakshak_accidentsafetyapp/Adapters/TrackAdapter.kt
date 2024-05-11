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
import com.example.rakshak_accidentsafetyapp.DataClasses.TrackItem
import com.example.rakshak_accidentsafetyapp.R
import java.util.Locale


class TrackAdapter(val context: Context, var list: ArrayList<TrackItem>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var itemList = arrayListOf<TrackItem>()
    init {
        itemList.clear()
        itemList.addAll(list)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.trackitem,parent,false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mem = itemList[position]
        val viewholder: ItemViewHolder = holder as ItemViewHolder

        Glide.with(context)
            .load(mem.url)
            .centerInside()
            .circleCrop()
            .into(viewholder.img)

        viewholder.name.text=mem.name
        viewholder.uid.text=mem.uid
        viewholder.mapBtn.setOnClickListener {
            val intent = Intent(context, LocationPointActivity::class.java)
            intent.putExtra("uid",mem.uid)
            context.startActivity(intent)
        }

    }


    fun updateList(newList:ArrayList<TrackItem>)
    {
        itemList.clear()
        itemList.addAll(newList)
        Log.i("busCheck", itemList.size.toString())
        notifyDataSetChanged()
    }


    class ItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var img: ImageView =itemView.findViewById(R.id.profilePic)
        var name:TextView = itemView.findViewById(R.id.name)
        var uid:TextView = itemView.findViewById(R.id.uid)
        var mapBtn:ImageView = itemView.findViewById(R.id.maptile)

    }
}