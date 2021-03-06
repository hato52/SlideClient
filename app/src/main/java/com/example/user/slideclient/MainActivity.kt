package com.example.user.slideclient

import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.UUID.fromString

class MainActivity : AppCompatActivity() {

    private val REQUEST_ENABLE_BT = 1
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var global_mySocket: BluetoothSocket? = null
    private var serverNameText: TextView? = null
    private val myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serverNameText = findViewById<TextView>(R.id.select_server)

        /**アプリ起動時にBluetooth有効かをチェック*/
        //val myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (myBluetoothAdapter == null) {
            Toast.makeText(this, "この端末では使えないよ(´・ω・`)", Toast.LENGTH_SHORT).show()
        }

        /**有効でなければインテントを発行する*/
        if (!myBluetoothAdapter.isEnabled()) {
            intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_ENABLE_BT)
        }

        /**ペアリング設定済みの端末をダイアログで表示*/
        val connect_button: Button = findViewById<Button>(R.id.connect_button) as Button
        connect_button.setOnClickListener{ view ->
            val pairedDevices: Set<BluetoothDevice> = myBluetoothAdapter.bondedDevices

            if (pairedDevices.size > 0) {
                showPairedDevicesList(pairedDevices)
            }
        }

        /**サーバ端末へデータを送信*/
        val send_button: Button = findViewById<Button>(R.id.send_button) as Button
        send_button.setOnClickListener{ view ->
            communicationThreadExec(global_mySocket, "TEST_DATA")  /**データ送信*/
            //Toast.makeText(this, "send data", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetoothが有効になりました", Toast.LENGTH_SHORT).show()
        }else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "なんで有効化してくれへんの??", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPairedDevicesList(pairedDevices: Set<BluetoothDevice>) {
        /**mutable <-> 変更可能な
         MutableListでなくListを使うと変更可能ではないため、addメソッドが使えない*/
        val deviceName: MutableList<String> = mutableListOf()
        val device: MutableList<BluetoothDevice> = mutableListOf()
        for (devices in pairedDevices) {
            device.add(devices)
            deviceName.add(devices.name)
        }

        /**リストビューのアダプタを作成*/
        val listView = ListView(this)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceName)
        listView.setAdapter(arrayAdapter)

        /**ダイアログを表示*/
        val builder = AlertDialog.Builder(this)
        builder.setTitle("端末を選択")
        builder.setView(listView)
        builder.setNegativeButton("閉じる", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()

        /**選択されたデバイスでスレッドオブジェクトを作成
         * ダイアログを閉じてスレッドを実行
         * */
        listView.setOnItemClickListener{parent,view, position, id ->
            serverNameText?.text = deviceName[position]
            dialog.dismiss()
            acceptThreadExec() /**サーバ端末に接続する*/
        }
    }

    /**サーバ端末に接続するスレッド */
    private fun acceptThreadExec() {

        var myServerSocket: BluetoothServerSocket?
        var tmp: BluetoothServerSocket? = null

        try {
            tmp = myBluetoothAdapter.listenUsingRfcommWithServiceRecord(this.packageName, MY_UUID)
        } catch (e: IOException) {
            println(e.printStackTrace())
        }
        myServerSocket = tmp

        Toast.makeText(this, "接続受け入れを開始", Toast.LENGTH_SHORT).show()

        object : AsyncTask<Void, Void, BluetoothSocket?>() {

            var mySocket: BluetoothSocket? = null

            override fun doInBackground(vararg params: Void): BluetoothSocket? {
                var mySocket: BluetoothSocket? = null

                while(true) {
                    try {
                        mySocket = myServerSocket?.accept()
                    } catch (connectException: IOException) {
                        return null
                        break
                    }

                    if(mySocket != null) {
                        myServerSocket?.close()
                        return mySocket
                        break
                    }
                }
            }

            override fun onPostExecute(result: BluetoothSocket?) {
                if (result != null) {
                    Toast.makeText(this@MainActivity, "接続に成功", Toast.LENGTH_SHORT).show()
                    Log.d("SUCCESS!!", "接続に成功")
                    global_mySocket = result
                    try {
                        mySocket?.close()
                    } catch (e: IOException) {
                        println(e.printStackTrace())
                    }
                }else{
                    Toast.makeText(this@MainActivity, "接続に失敗", Toast.LENGTH_SHORT).show()
                    Log.d("FAILED!!", "接続に失敗")
                    global_mySocket = null
                    try {
                        mySocket?.close()
                    } catch (e: IOException) {
                        println(e.printStackTrace())
                    }
                }
            }

            override fun onCancelled() {
                super.onCancelled()
                mySocket?.close()
            }
        }.execute()
    }

    /**クライアント端末へのデータの送信を行うスレッド*/
    private fun communicationThreadExec(socket: BluetoothSocket?, send_data: String) {
        val mySocket: BluetoothSocket? = socket
        var myInStream: InputStream? = null
        var myOutStream: OutputStream? = null

        if (socket == null) {
            Toast.makeText(this, "ソケットが存在しません", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            myInStream = socket?.inputStream
            myOutStream = socket?.outputStream
        } catch (e: IOException) {
            println(e.printStackTrace())
        }

        object : AsyncTask<ByteArray, Void, Boolean>(){
            override fun doInBackground(vararg params: ByteArray): Boolean? {
                if (params == null) {
                    return false
                }
                try {
                    myOutStream?.write(params[0])
                } catch (e: IOException) {
                    println(e.printStackTrace())
                    return false
                }
                return true
            }

            override fun onPostExecute(result: Boolean) {
                if (result) {
                    Toast.makeText(this@MainActivity, "送信完了", Toast.LENGTH_SHORT).show()
                    Log.d("SUCCESS!!", "送信完了")
                }else{
                    Toast.makeText(this@MainActivity, "送信失敗", Toast.LENGTH_SHORT).show()
                    Log.d("FAILED!!", "送信失敗")
                    try {
                        mySocket?.close()
                    } catch (e: IOException) {
                        println(e.printStackTrace())
                    }
                }
            }

            override fun onCancelled() {
                super.onCancelled()
                mySocket?.close()
            }
        }.execute(send_data.toByteArray())
    }
}
