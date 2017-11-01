package com.example.user.slideclient

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.IOException

/**
 * Created by Souta on 2017/11/01.
 */
class ConnectThread(device: BluetoothDevice) : Thread() {
    private var mySocket: BluetoothSocket? = null
    private val myDevice: BluetoothDevice = device

    init {
        var tmp: BluetoothSocket? = null

        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID)
        }
        catch (e: IOException) {
            mySocket = tmp
        }
    }

    override fun run() {
        try {
            mySocket?.connect()
        }
        catch (connectException: IOException) {
            try {
                mySocket?.close()
            }
            catch (closeException: IOException) {
                return
            }
        }

        //接続に成功したらここで通信メソッドを実行
    }

    public fun cancel() {
        try {
            mySocket?.close()
        }
        catch (e: IOException) {}
    }

}