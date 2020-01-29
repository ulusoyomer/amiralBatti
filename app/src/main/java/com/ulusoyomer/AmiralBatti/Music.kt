package com.ulusoyomer.AmiralBatti

import android.content.Context
import android.media.MediaPlayer

class Music(var context: Context) {
    var main_theme_player = MediaPlayer.create(context,R.raw.main_theme)
    var btn_theme_player = MediaPlayer.create(context,R.raw.btn_theme)
    init {
        main_theme_player.isLooping = true
    }
    fun playerMainStart(){
        main_theme_player.start()
    }
    fun playerMainStop(){
        main_theme_player.pause()
    }

    fun playerBtnStart(){
        btn_theme_player.start()
    }
}