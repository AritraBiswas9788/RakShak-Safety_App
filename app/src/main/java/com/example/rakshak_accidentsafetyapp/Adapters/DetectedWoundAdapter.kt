package com.example.rakshak_accidentsafetyapp.Adapters

import android.content.Context
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
import com.example.rakshak_accidentsafetyapp.R
import java.util.Locale


class DetectedWoundAdapter(val context: Context, var list: ArrayList<Pair<String,String>>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val remedy = arrayListOf(
        "Clean the area: Gently rinse the wound with clean water to remove any debris and Stop the bleeding: Apply gentle pressure with a clean cloth or sterile bandage.  dirt. Avoid scrubbing as this can cause more skin damage. \n Apply antiseptic: Use an antiseptic solution or wipe to disinfect the area. \n Cover: Use a sterile bandage or dressing to protect the abrasion from bacteria and to keep it clean.",
        "Stop the bleeding: Apply gentle pressure with a clean cloth or sterile bandage.\n Clean the wound: Rinse under clean water and gently wash around the wound with soap and a clean cloth. \n Apply an antibiotic cream: To reduce the risk of infection.\n Bandage: Cover the cut with a sterile dressing. ",
        "Cold compress: Apply a cold pack or a cloth cooled with cold water to the bruised area for about 20 minutes to reduce swelling and bleeding. \n Elevate: If possible, keep the bruised area elevated above the level of the heart to reduce swelling. \n Rest: Avoid further strain on the bruised area for a few days. ",
        "Cool the burn: Hold the burned area under cool (not cold) running water for 10-15 minutes or apply a clean, cool, wet cloth. \n Remove constricting items: Gently remove any jewelry or clothing around the burn, if possible before swelling starts. \n Cover: Use a sterile non-adhesive bandage or clean cloth. \n Do not apply ointments or butter: These can cause infection and trap heat.",
        "Stop the bleeding: Apply pressure with a clean cloth or sterile bandage. If blood soaks through, place another bandage on top. \n Clean the wound lightly: If the wound is shallow, rinse it under running water. Remove any debris or dirt if visible. \n Cover: Use a clean cloth or sterile bandage to protect the wound until medical help is received. ",
        "Do Not Remove the Object: If the object that caused the stab wound is still in place, do not remove it. Removing it could cause more damage or increase bleeding.\n Control Bleeding: Apply pressure to the wound with a clean cloth, bandage, or if not available, a piece of clothing. If the object is still in the wound, apply pressure around the object, not directly over it. \n Elevate the Injured Area: If possible and does not cause further pain, gently elevate the area of the wound above the level of the heart to help reduce bleeding. "
    ,"Its Clean")

    val wounds = arrayOf("abrasions","cut","bruise","burn","laceration","stab","clean")
    val resView = arrayListOf(R.drawable.abrasion,R.drawable.cuts,R.drawable.bruise,R.drawable.laceration,R.drawable.stab,R.drawable.sound)
    var isOn = false
    var itemList = arrayListOf<Pair<String,String>>()
    private lateinit var tts: TextToSpeech
    init {
        itemList.clear()
        itemList.addAll(list)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.wounditem,parent,false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wound = itemList[position]
        val viewholder: ItemViewHolder = holder as ItemViewHolder

        viewholder.woundText.text=wound.first
        viewholder.bodyText.text=wound.second
        val index = wounds.indexOf(wound.first)
        viewholder.img.setImageDrawable(context.resources.getDrawable(resView[index]))
        viewholder.remedyText.text=remedy[index]
        viewholder.soundBtn.setOnClickListener {
            if(!isOn) {
                isOn = true
                viewholder.soundBtn.setImageDrawable(context.resources.getDrawable(R.drawable.muted))
                startSpeech(remedy[index])
            }
            else
            {
                isOn = false
                viewholder.soundBtn.setImageDrawable(context.resources.getDrawable(R.drawable.sound))
                tts?.stop()
                tts?.shutdown()
            }
        }


    }
    fun updateList(newList:ArrayList<Pair<String,String>>)
    {
        itemList.clear()
        itemList.addAll(newList)
        Log.i("busCheck", itemList.size.toString())
        notifyDataSetChanged()
    }

    fun startSpeech(text:String)
    {
        tts = TextToSpeech(context, OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {

                val desiredLocale = Locale.US // Change to the desired language/locale
                tts.language = desiredLocale
                val voices = tts.voices
                val voiceList: List<Voice> = ArrayList(voices)
                val selectedVoice = voiceList[0] // Change to the desired voice index
                tts.voice = selectedVoice
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Log.i("failed","tts service fail")
            }
        })
    }

    class ItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var img: ImageView =itemView.findViewById(R.id.woundIcon)
        var woundText:TextView = itemView.findViewById(R.id.Wound)
        var bodyText:TextView = itemView.findViewById(R.id.Body)
        var remedyText:TextView = itemView.findViewById(R.id.Remedy)
        var soundBtn:ImageView = itemView.findViewById(R.id.soundButton)

    }
}