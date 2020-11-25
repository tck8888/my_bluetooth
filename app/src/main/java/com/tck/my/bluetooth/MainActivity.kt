package com.tck.my.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @see https://github.com/android/connectivity-samples/tree/main/BluetoothLeChat
 */
class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "tck6666"
        val PERMISSIONS_REQUEST_CODE = 100
        val REQUEST_ENABLE_BT = 1000
        val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
        fun hasPermission(context: Context): Boolean {
            return PERMISSIONS.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
        }

    }

    private val bluetoothDevices = ArrayList<BluetoothDeviceModel>()
    private lateinit var adapter: BluetoothDeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_start_scan.setOnClickListener {
            startScanWithPermission()
        }
        rv_bluetooth.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        adapter = BluetoothDeviceAdapter(this, bluetoothDevices)
        rv_bluetooth.adapter =adapter

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    private fun startScanWithPermission() {
        if (hasPermission(this)) {
            startScan()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val all = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (all) {
                startScan()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startScan() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "当前设备不支持蓝牙", Toast.LENGTH_LONG).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            val bondedDevices = bluetoothAdapter.bondedDevices
            if (bondedDevices.isNullOrEmpty()) {
                Log.d(TAG, "bondedDevices isNullOrEmpty ")
            } else {
                bondedDevices.forEachIndexed { index, bluetoothDevice ->
                    Log.d(TAG, "deviceName:${bluetoothDevice.name},deviceHardwareAddress:${bluetoothDevice.address}")
                }
            }

            bluetoothAdapter.startDiscovery()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "open bluetooth success")
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action: String = intent?.action ?: ""
            if (action == BluetoothDevice.ACTION_FOUND) {
                val device: BluetoothDevice? = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = device?.name ?: ""
                val deviceHardwareAddress = device?.address ?: ""
                Log.d(TAG, "deviceName:${deviceName},deviceHardwareAddress:${deviceHardwareAddress}")
                if (deviceName.isNotEmpty() && deviceHardwareAddress.isNotEmpty()) {
                    bluetoothDevices.add(BluetoothDeviceModel(deviceName, deviceHardwareAddress))
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}