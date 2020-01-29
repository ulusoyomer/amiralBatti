package com.ulusoyomer.AmiralBatti

import android.widget.Button

class GameBoard(var button: Button) {
    var isSea = false
    var isShip = false
    var isDamagedShip = false
    var isDamegedSea = false
    var isClosedSea = true
    var id = 0

    fun doItSea() {
        isSea = true
        button.setBackgroundResource(R.drawable.gameboardbtn_sea)
        isClosedSea = false
        isShip = false
        isDamegedSea = false
        isDamagedShip = false
    }

    fun doItShip() {
        isShip = true
        button.setBackgroundResource(R.drawable.gameboardbtn_green)
        isSea = false
        isDamagedShip = false
        isDamegedSea = false
        isClosedSea = false
    }

    fun doItDamagedShip() {
        isDamagedShip = true
        button.setBackgroundResource(R.drawable.gameboardbtn_red)
        isShip = false
        isSea = false
        isDamegedSea = false
        isClosedSea = false
    }

    fun doItDamagedSea() {
        isDamegedSea = true
        button.setBackgroundResource(R.drawable.gameboardbtn_orange)
        isSea = false
        isShip = false
        isSea = false
        isClosedSea = false
    }

    fun doItClosedSea() {
        isClosedSea = true
        button.setBackgroundResource(R.drawable.gameboardbtn_darksea)
        isSea = false
        isShip = false
        isDamagedShip = false
        isDamegedSea = false
    }
    fun doItMyturn(){
        button.setBackgroundResource(R.drawable.gameboardbtn_sea)
    }
    fun doItEnemyTurn(){
        button.setBackgroundResource(R.drawable.gameboardbtn_darksea)
    }
}