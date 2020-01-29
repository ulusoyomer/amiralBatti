package com.ulusoyomer.AmiralBatti

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Message
import java.io.IOException
import java.util.*

class ClientClass(device: BluetoothDevice, MY_UUID: UUID) : Thread(), ISTATE {
    private var socket: BluetoothSocket? = null

    init {
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        try {
            socket!!.connect()
            var message: Message = Message.obtain()
            message.what = STATE_CONNECTED
            NecessaryHandler.handler.sendMessage(message)

            GameActivity.sendReceive = SendReceive(socket!!)
            GameActivity.sendReceive!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
            var message: Message = Message.obtain()
            message.what = STATE_CONNECTION_FAILED
            NecessaryHandler.handler.sendMessage(message)
        }
    }
}