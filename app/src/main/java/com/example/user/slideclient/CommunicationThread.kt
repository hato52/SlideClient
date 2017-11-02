package com.example.user.slideclient

import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Created by User on 2017/11/02.
 *
 * サーバ端末との通信用スレッド
 */
class CommunicationThread(socket: BluetoothSocket?) : Thread() {
    private val mySocket: BluetoothSocket? = socket
    private var myInStream: InputStream? = null
    private var myOutStream: OutputStream? = null

    init {
        try {
            myInStream = socket?.inputStream
            myOutStream = socket?.outputStream
        }
        catch (e: IOException) {
            println(e.printStackTrace())
        }
    }

    override fun run() {

    }

    fun write(sendData: ByteArray) {
        try {
            myOutStream?.write(sendData)
        }
        catch (e: IOException) {
            println(e.printStackTrace())
        }
    }

    fun  cancel() {
        try {
            mySocket?.close()
        }
        catch (e: IOException) {
            println(e.printStackTrace())
        }
    }
}