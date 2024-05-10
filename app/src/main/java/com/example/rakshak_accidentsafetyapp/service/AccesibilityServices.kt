package com.example.rakshak_accidentsafetyapp.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.rakshak_accidentsafetyapp.Activity.AccidentTimerActivity
import kotlin.math.sqrt


class AccesibilityServices: AccessibilityService(), SensorEventListener {
    var lastState = false
    var isaccelerometerp=false
    var xvalue:Float =0.0f
    var yvalue:Float=0.0f
    var zvalue:Float=0.0f
    var mainvalue:Float=0.0f
    var flag:Boolean=false

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }
    override fun onInterrupt() {

    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        Log.i("ServiceChecker", "Service-Connected")
        Log.i("ServiceChecker", "Check-ran")

//        while(!detectAccident())
//        {
//
        detectAccident()
//
//
//
//        }
    }

    private fun launchApp() {

        try {
            Log.i("ServiceChecker", this.toString())
            Log.i("ServiceChecker", applicationContext.toString())
            val intent = Intent(applicationContext, AccidentTimerActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.`package`= "com.example"
            applicationContext.startActivity(intent)
        }
        catch(e:Exception)
        {
            Log.e("ServiceChecker", e.toString())
        }


    }

    private fun detectAccident() {

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if(sensor!=null)
        { isaccelerometerp=true
            Log.d("check_A","Accelorometer sensor is  present in the phone")
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL)
        }
        else
        {
            Log.d("check_A","Accelorometer sensor is not present in the phone")
            isaccelerometerp=false
        }

//       mainvalue=sqrt(xvalue*xvalue+yvalue*yvalue+zvalue*zvalue)
//        Log.d("valueacceleration","${mainvalue}")

//        return false

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if(event.sensor.type==Sensor.TYPE_ACCELEROMETER)
            {
                Log.i("inside sensor","yo")
                xvalue= event!!.values[0]
                yvalue= event.values[0]
                zvalue= event.values[0]
//                mainvalue=sqrt(xvalue*xvalue+yvalue*yvalue+zvalue*zvalue)
//                Log.d("valueacceleration","${mainvalue}")
                Log.d("xvalue","${xvalue } ${yvalue} ${zvalue} ")
                Log.d("yvalue",yvalue.toString())
                Log.d("zvalue",zvalue.toString())
                mainvalue=sqrt(xvalue*xvalue+yvalue*yvalue+zvalue*zvalue)
                Log.d("valueacceleration","${mainvalue}")
                if(mainvalue>=75)
                {
                    flag=true
                    launchApp()
                }

            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.i("inside accuracy","yo")
        }


}
