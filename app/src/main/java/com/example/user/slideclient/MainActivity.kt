package com.example.user.slideclient

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private val REQUEST_ENABLE_BT = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //アプリ起動時にBluetooth有効かをチェック
        val myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (myBluetoothAdapter == null) {
            Toast.makeText(this, "この端末では使えないよ(´・ω・`)", Toast.LENGTH_SHORT).show()
        }
        //有効でなければインテントを発行する
        if (!myBluetoothAdapter.isEnabled()) {
            intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_ENABLE_BT)
        }

        //ペアリング設定済みの端末をダイアログで表示
        val connect_button: Button = findViewById<Button>(R.id.connect_button) as Button
        connect_button.setOnClickListener{ view ->
            val pairedDevices: Set<BluetoothDevice> = myBluetoothAdapter.bondedDevices

            if (pairedDevices.size > 0) {
                showPairedDevicesList(pairedDevices)
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

    private fun showPairedDevicesList(pairedDevices: Set<BluetoothDevice>) {
        //mutable <-> 変更可能な
        //MutableListでなくListを使うと変更可能ではないため、addメソッドが使えない
        val deviceName: MutableList<String> = mutableListOf()
        for (device in pairedDevices) {
            deviceName.add(device.name)
        }

        //リストビューのアダプタを作成
        val listView = ListView(this)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceName)
        listView.setAdapter(arrayAdapter)

        //ダイアログを表示
        val builder = AlertDialog.Builder(this)
        builder.setTitle("端末を選択")
        builder.setView(listView)
        builder.setNegativeButton("閉じる", null)
        builder.show()
    }
}
