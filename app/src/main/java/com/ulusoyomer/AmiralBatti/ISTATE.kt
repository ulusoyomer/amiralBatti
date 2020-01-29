package com.ulusoyomer.AmiralBatti

import java.util.*

interface ISTATE {
    val STATE_LISTENING: Int
        get() = 1
    val STATE_CONNECTING: Int
        get() = 2
    val STATE_CONNECTED: Int
        get() = 3
    val STATE_CONNECTION_FAILED: Int
        get() = 4
    val STATE_MESSAGE_RECEIVED: Int
        get() = 5
    val MY_UUID: UUID
        get() = UUID.fromString("7eb72855-94ea-48a7-93de-6c669244709a")
}