package com.example.orientation.aidl.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.orientation.aidl.OrientationInterface
import com.example.orientation.aidl.service.OrientationService
import com.example.orientation.aidl.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var orientationInterface: OrientationInterface? = null
    private var orientationServiceConnection: ServiceConnection? = null
    private var orientationServiceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        OrientationService.sensorData.observe(this, Observer {
            tvOrientation?.text = it.contentToString()
        })

        orientationInterface?.orientation()?.let {
            tvOrientation?.text = it
        }
        bindOrientationService()
    }

    private fun bindOrientationService() {
        orientationServiceIntent = Intent(this, OrientationService::class.java)
        orientationServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
                orientationInterface = OrientationInterface.Stub.asInterface(binder)
                orientationInterface?.orientation()?.let {
                    tvOrientation?.text = it
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {
            }
        }
        orientationServiceIntent?.let {
            orientationServiceConnection?.let {
                bindService(
                    orientationServiceIntent,
                    it,
                    Context.BIND_AUTO_CREATE
                )
            }
        }
    }
}