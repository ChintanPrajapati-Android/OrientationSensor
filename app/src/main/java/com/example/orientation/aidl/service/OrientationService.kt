package com.example.orientation.aidl.service

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.orientation.aidl.OrientationInterface

class OrientationService : LifecycleService(), SensorEventListener {

    companion object {
        private const val REFRESH_TIME = 8000
        val sensorData = MutableLiveData<FloatArray>()
    }

    private var sensorManager: SensorManager? = null
    private var rotationSensor: Sensor? = null

    private fun init() {
        if (sensorManager == null) {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            rotationSensor?.let {
                addRotationSensorListener()
            }
        }
    }

    private fun addRotationSensorListener() {
        sensorManager?.registerListener(
            this,
            rotationSensor,
            REFRESH_TIME
        )
    }

    private val myBinder: OrientationInterface.Stub = object : OrientationInterface.Stub() {
        override fun orientation(): String {
            init()
            return sensorData.value?.contentToString() ?: "Get data"
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return myBinder
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let {
            if (it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                sensorData.value = it.values
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}
