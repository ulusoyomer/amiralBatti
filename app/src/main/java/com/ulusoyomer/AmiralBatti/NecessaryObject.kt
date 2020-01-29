package com.ulusoyomer.AmiralBatti

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

object NecessaryObject {
    fun noActionBar(context: AppCompatActivity) {
        context.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    fun noOffScreen(context: AppCompatActivity) {
        context.window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
    }
}