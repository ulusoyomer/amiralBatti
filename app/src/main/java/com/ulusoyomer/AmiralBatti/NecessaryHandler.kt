package com.ulusoyomer.AmiralBatti

import android.content.Context
import android.content.Intent
import android.os.Handler
import com.blogspot.atifsoftwares.animatoolib.Animatoo

object NecessaryHandler : ISTATE {
    var contex: Context? = null
    var handler = Handler(Handler.Callback { msg ->
        when (msg.what) {
            STATE_LISTENING -> {

            }
            STATE_CONNECTING -> {

            }
            STATE_CONNECTED -> {
                var intent = Intent(contex, GameActivity::class.java)
                intent.putExtra("Game Mod", "Multiplayer")
                contex!!.startActivity(intent)
                Animatoo.animateSlideDown(contex)
            }
            STATE_CONNECTION_FAILED -> {

            }
        }
        true
    })
}