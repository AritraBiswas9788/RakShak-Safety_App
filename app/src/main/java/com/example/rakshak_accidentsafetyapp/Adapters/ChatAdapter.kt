package com.example.rakshak_accidentsafetyapp.Adapters

import android.content.Context
import android.content.Intent

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
import com.example.rakshak_accidentsafetyapp.DataClasses.Message
import com.example.rakshak_accidentsafetyapp.DataClasses.TrackItem
import com.example.rakshak_accidentsafetyapp.R

import java.util.Locale


class ChatAdapter(val context: Context, var list: ArrayList<Message>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var itemList = arrayListOf<Message>()
    init {
        itemList.clear()
        itemList.addAll(list)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.chatitem,parent,false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = itemList[position]
        val viewholder: ItemViewHolder = holder as ItemViewHolder

        if(chat.sender=="model")
        {
            viewholder.botTxt.visibility=View.VISIBLE
            viewholder.botImg.visibility=View.VISIBLE
            viewholder.manImg.visibility=View.GONE
            viewholder.manTxt.visibility=View.GONE

            viewholder.botTxt.text=chat.text
        }
        else
        {
            viewholder.botTxt.visibility=View.GONE
            viewholder.botImg.visibility=View.GONE
            viewholder.manImg.visibility=View.VISIBLE
            viewholder.manTxt.visibility=View.VISIBLE

            viewholder.manTxt.text=chat.text
        }


    }


    fun updateList(newList:ArrayList<Message>)
    {
        itemList.clear()
        itemList.addAll(newList)
        Log.i("busCheck", itemList.size.toString())
        notifyDataSetChanged()
    }


    class ItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var botImg: ImageView =itemView.findViewById(R.id.botImg)
        var botTxt:TextView = itemView.findViewById(R.id.botText)
        var manImg:ImageView = itemView.findViewById(R.id.humanImg)
        var manTxt:TextView = itemView.findViewById(R.id.humanText)

    }
}