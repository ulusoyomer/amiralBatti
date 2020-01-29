package com.ulusoyomer.AmiralBatti

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Message
import java.io.IOException
import java.util.*

class ServerClass(bluetoothAdapter: BluetoothAdapter?, MY_UUID: UUID) : Thread(), ISTATE {
    private var serverSocket: BluetoothServerSocket? = null

    init {
        try {
            serverSocket =
                bluetoothAdapter!!.listenUsingRfcommWithServiceRecord("AmiralBatti", MY_UUID)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        var socket: BluetoothSocket? = null
        while (socket == null) {
            try {
                var message: Message = Message.obtain()
                message.what = STATE_CONNECTING
                NecessaryHandler.handler.sendMessage(message)
                socket = serverSocket!!.accept()

            } catch (e: IOException) {
                e.printStackTrace()
                var message: Message = Message.obtain()
                message.what = STATE_CONNECTION_FAILED
                NecessaryHandler.handler.sendMessage(message)
            }
            if (socket != null) {
                var message: Message = Message.obtain()
                message.what = STATE_CONNECTED
                NecessaryHandler.handler.sendMessage(message)
                GameActivity.sendReceive = SendReceive(socket)
                GameActivity.sendReceive!!.start()
                break
            }
        }
    }

}