package com.ulusoyomer.AmiralBatti

import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class SendReceive(bluetoothSocket: BluetoothSocket) : Thread(), ISTATE {
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    init {
        var tempIn: InputStream? = null
        var tempOut: OutputStream? = null
        try {
            tempIn = bluetoothSocket.inputStream
            tempOut = bluetoothSocket.outputStream
        } catch (e: IOException) {
            e.printStackTrace()
        }
        inputStream = tempIn
        outputStream = tempOut
    }

    override fun run() {
        var buffer = ByteArray(1024)
        var bytes: Int? = null

        while (true) {
            try {
                bytes = inputStream!!.read(buffer)
                GameActivity.handler!!.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer)//*****************************
                    .sendToTarget()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun write(bytes: ByteArray) {
        try {
            outputStream!!.write(bytes)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}