package com.example.schandiramani.simplecircleview

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu

class MainActivity :  AppCompatActivity(), SensorEventListener {
    lateinit var userTouchView: UserTouchView
    var sensorManager: SensorManager?  = null
    var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_main)
        userTouchView = findViewById(R.id.touch)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }


    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    public override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {
        userTouchView.onSensorChanged(event)
    }

}
