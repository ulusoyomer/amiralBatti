package com.ulusoyomer.AmiralBatti

import android.content.Context
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView

class Animations(var context: Context,btn_Single:Button,btn_Volume:Button,btn_Multi:Button,iv_Logo:ImageView,btn_listenDevice:Button) {
    var logo_animation = AnimationUtils.loadAnimation(context,R.anim.logo_anim)
    var button_animation = AnimationUtils.loadAnimation(context,R.anim.buton_anim)
    init {
        iv_Logo.animation = logo_animation
        btn_Multi.animation = button_animation
        btn_Volume.animation = button_animation
        btn_Single.animation = button_animation
        btn_listenDevice.animation = button_animation
    }
}