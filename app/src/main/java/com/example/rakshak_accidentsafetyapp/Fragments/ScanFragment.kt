package com.example.rakshak_accidentsafetyapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rakshak_accidentsafetyapp.Activity.CameraXLivePreviewActivity
import com.example.rakshak_accidentsafetyapp.Adapters.ChatAdapter
import com.example.rakshak_accidentsafetyapp.DataClasses.Message
import com.example.rakshak_accidentsafetyapp.R
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ScanFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var chatRecView:RecyclerView
    lateinit var chatRecAdapter:ChatAdapter
    lateinit var sendBtn: ImageView
    lateinit var textField:TextInputEditText
    var chatList = arrayListOf<Message>()
    var isOn = true
    private lateinit var tts: TextToSpeech
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

        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val intent = Intent(context,CameraXLivePreviewActivity::class.java)
        startActivity(intent)

        chatRecView=view.findViewById(R.id.chatList)
        sendBtn=view.findViewById(R.id.sender)
        textField=view.findViewById(R.id.inputPrompt)

        chatRecAdapter= ChatAdapter(requireContext(),chatList)
        chatRecView.adapter=chatRecAdapter
        chatRecView.layoutManager=LinearLayoutManager(context)


        val scanBtn = view.findViewById<ImageView>(R.id.scanButton)
        val volumeBtn = view.findViewById<ImageView>(R.id.speakerButton)

        scanBtn.setOnClickListener {
            val intent = Intent(context,CameraXLivePreviewActivity::class.java)
            startActivity(intent)
        }
        volumeBtn.setOnClickListener {
            if(!isOn) {
                isOn = true
                volumeBtn.setImageDrawable(context?.resources?.getDrawable(R.drawable.muted))

            }
            else
            {
                isOn = false
                volumeBtn.setImageDrawable(context?.resources?.getDrawable(R.drawable.sound))
                tts?.stop()
                tts?.shutdown()
            }
        }





        sendBtn.setOnClickListener {
            if(textField.text?.isNotEmpty() == true)
            {
                val generativeModel = GenerativeModel(
                    // Use a model that's applicable for your use case (see "Implement basic use cases" below)
                    modelName = "gemini-pro",
                    // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                    apiKey ="AIzaSyB_0seDMgD5Dzo0-eTeWyoQdYprJqI6fsQ"
                )

                var history = arrayListOf<Content>()
                for (chat in chatList)
                    history.add(content(role = chat.sender) { text(chat.text) })

                val chat = generativeModel.startChat(
                    history = history)

                chatList.add(Message("user",textField.text.toString()))
                chatRecAdapter.updateList(chatList)
                textField.setText("")
                lifecycleScope.launch {
                    val response = chat.sendMessage(textField.text.toString())
                    startSpeech(response.text!!)
                    chatList.add(Message("model",response.text.toString()))
                    chatRecAdapter.updateList(chatList)
                }

            }
        }
    }
    fun startSpeech(text:String)
    {
        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {

                val desiredLocale = Locale.US // Change to the desired language/locale
                tts.language = desiredLocale
                val voices = tts.voices
                val voiceList: List<Voice> = ArrayList(voices)
                val selectedVoice = voiceList[0] // Change to the desired voice index
                tts.voice = selectedVoice
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                Log.i("failed", "tts service fail")
            }
        })
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}