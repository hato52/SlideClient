package com.example.user.slideclient

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val REQUEST_ENABLE_BT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (myBluetoothAdapter == null) {
            Toast.makeText(this, "この端末では使えないよ(´・ω・`)", Toast.LENGTH_SHORT).show()
        }

        if (!myBluetoothAdapter.isEnabled()) {
            intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_ENABLE_BT)
        }

        val connect_button: Button = findViewById<Button>(R.id.connect_button) as Button
        connect_button.setOnClickListener{ view ->
            val pairedDevices: Set<BluetoothDevice>? = myBluetoothAdapter.bondedDevices

            if (pairedDevices!!.size > 0) {
                
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetoothが有効になりました", Toast.LENGTH_SHORT).show()
        }else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "なんで有効化してくれへんの??", Toast.LENGTH_SHORT).show()
        }
    }
}
