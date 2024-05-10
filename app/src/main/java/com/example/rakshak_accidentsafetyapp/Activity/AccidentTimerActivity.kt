package com.example.rakshak_accidentsafetyapp.Activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.rakshak_accidentsafetyapp.CircleProgressBar
import com.example.rakshak_accidentsafetyapp.R
import com.ncorti.slidetoact.SlideToActView


class AccidentTimerActivity : AppCompatActivity() {
    private lateinit var timer: CircleProgressBar
    private var cdt:CountDownTimer? = null
    private lateinit var slideBtn:com.ncorti.slidetoact.SlideToActView
    private lateinit var time:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accident_timer)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        timer=findViewById(R.id.custom_progressBar)
        slideBtn=findViewById(R.id.slidebtn)
        time=findViewById(R.id.time)
        cdt = object : CountDownTimer(10000, 1000) {

            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                timer.setProgressWithAnimation((millisUntilFinished.toFloat()/10000.0 * 100.0).toFloat())
                time.text=(millisUntilFinished/1000).toInt().toString()+"s"
            }

            // Callback function, fired
            // when the time is up
            override fun onFinish() {
                Log.i("checkTime","done.")
                val intent = Intent(this@AccidentTimerActivity, SOSActivity::class.java)
                startActivity(intent)
            }
        }.start()
        slideBtn.onSlideCompleteListener = object :SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                Log.i("finishTest","killed.")
                finish()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cdt?.cancel()
    }
}