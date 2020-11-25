package com.tck.my.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.tck.my.bluetooth.MainActivity.Companion.TAG
import java.util.*


/**
 *<p>description:</p>
 *<p>created on: 2020/11/25 13:44</p>
 * @author tck
 *
 */
class Server(val bluetoothAdapter: BluetoothAdapter) : Thread() {

    val mmServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("tck", UUID.randomUUID())


    override fun run() {
        super.run()
        var shouldLoop = true
        while (shouldLoop){
            val socket: BluetoothSocket? =try {
                mmServerSocket?.accept()
            } catch (e: Exception) {
                Log.e(TAG, "Socket's accept() method failed", e)
                shouldLoop = false
                null
            }
            socket?.also {
                mmServerSocket?.close()
                shouldLoop = false
            }
        }
    }
}