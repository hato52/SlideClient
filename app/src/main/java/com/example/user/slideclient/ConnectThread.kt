package com.example.user.slideclient

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.util.*

/**
 * Created by Souta on 2017/11/01.
 *
 * サーバ端末への接続用スレッド
 */
class ConnectThread(device: BluetoothDevice) : Thread() {
    private val myDevice: BluetoothDevice = device
    private var mySocket: BluetoothSocket? = null

    private val MY_UUID: UUID = UUID.fromString("a294fa24-c133-4875-86ac-0a0fe266a25a")

    //ソケットの取得
    init {
        try {
            mySocket = device.createRfcommSocketToServiceRecord(MY_UUID)
        }
        catch (e: IOException) {
            println(e.printStackTrace())
        }
    }

    //接続の開始
    override fun run() {
        try {
            mySocket?.connect()
        }
        catch (connectException: IOException) {
            try {
                mySocket?.close()
            }
            catch (closeException: IOException) {
                println(closeException.printStackTrace())
            }

            return
        }
    }

    fun cancel() {
        try {
            mySocket?.close()
        }
        catch (e: IOException) {
            println(e.printStackTrace())
        }
    }

}