package com.ulusoyomer.AmiralBatti


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.github.pierry.simpletoast.SimpleToast
import kotlinx.android.synthetic.main.activity_main.btn_Volume
import kotlinx.android.synthetic.main.activity_single_game.*
import kotlinx.android.synthetic.main.game_final_dialog.view.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import kotlin.system.exitProcess


class GameActivity : AppCompatActivity(), ISTATE {
    companion object {
        lateinit var handler: Handler
        var sendReceive: SendReceive? = null

    }

    var box_List = ArrayList<GameBoard>()
    var game_start_close_state: String = "New"
    private var state =
        0 //Oyunumuzun durmuna göre değişir state 0 ken gemiz yerleştirebiliriz çıkarabiliriz 1 olrrsa tüm gemiler yerleşmiştir gibi oyunu durumun anlamamıza yarar.
    private var ship_List = ArrayList<Button>()
    private var selected_ship: Button? = null
    private var selected_box: Button? = null
    private var Id =
        1 // gemileri yerleştirirken farklı gemilerin idleri farkı olması için tanımlandı bir gemi yerleştirinlce artıyor
    private var direction =
        0 // gemilerin dik veya yan yerleştirmek istediğimzde değişir ona göre fonksiyonun bir kısmı çalışır.
    private var game_mod: String =
        "" // Oyunun Single playmi yoksa multiplaymi olduna göre setlenir ona göre oyuna yapay düşan eklenir MainActivityden gelir değeri
    private var backTime: Long =
        0 // Oyundan geri çıkıldıgında popüler oygulaamarda gördüğümüz 2sn içinde bir daha basarsanız çıkarsınız ygulaması için vuurent time tutan değişken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_game)
        NecessaryObject.noActionBar(this)
        NecessaryObject.noOffScreen(this)
        loopThrough(findViewById<ViewGroup>(R.id.layout_root))
        openCloseRemovebutton()
        openCloseRevolvebutton()
        setHandler()
        tv_notificationTable.isVisible = false
        tv_notificationTable.movementMethod = object : ScrollingMovementMethod() {}
        var extras = intent.extras
        if (extras != null) {
            game_mod = extras.getString("Game Mod").toString()
        }
        if (game_mod != "Single Game") {
            game_start_close_state = "M"
        }

    }

    override fun onBackPressed() {
        if (game_start_close_state == "M") {
            if (backTime + 2000 > System.currentTimeMillis()) {
                object : CountDownTimer(2000, 2000) {
                    override fun onFinish() {
                        finish()
                        exitProcess(0)
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        sendReceive?.write("Disconnect".toByteArray())
                        SimpleToast.info(this@GameActivity, "Exiting...")
                    }

                }.start()
            } else {
                SimpleToast.info(this, "Press Back Again to Exit")
            }
        } else if (game_start_close_state == "Close Game") {
            object : CountDownTimer(2000, 2000) {
                override fun onFinish() {
                    finish()
                    exitProcess(0)
                }

                override fun onTick(millisUntilFinished: Long) {
                    SimpleToast.info(this@GameActivity, "Exiting...")
                }

            }.start()
        }
        if (game_mod == "Single Game") {
            if (game_start_close_state == "direct") {
                super.onBackPressed()
                cleanGame()
                Animatoo.animateSlideRight(this)
                MainActivity.app_state_mode = "Back Activity"
            } else {
                if (backTime + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed()
                    cleanGame()
                    Animatoo.animateSlideRight(this)
                    MainActivity.app_state_mode = "Back Activity"
                } else {
                    SimpleToast.info(this, "Press Back Again to Exit")
                }
            }
        }
        backTime = System.currentTimeMillis()
    }


    override fun onStop() {
        super.onStop()
        if (MainActivity.app_state_mode == "Background2") {
            MainActivity.music?.playerMainStop()
        }

    }

    override fun onStart() {
        super.onStart()
        if (MainActivity.app_state_mode == "Background2")
            MainActivity.music?.playerMainStart()
        volumeControl()
    }

    // sesin açıkmı kapalımı oldugunu kontrol eden fonksiyon
    fun volumeControl() {
        if (!(MainActivity.music?.main_theme_player?.isPlaying!!))
            btn_Volume.setBackgroundResource(R.drawable.volume_off)
        else
            btn_Volume.setBackgroundResource(R.drawable.volume_on)
        if (MainActivity.app_state_mode != "Background2") {
            MainActivity.app_state_mode = "Background2"
        }
    }

    //Layoutumuzdaki buttonları array liste atan fonksiyon
    fun loopThrough(parent: ViewGroup) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)

            if (child is Button) {
                if (!child.id.equals(R.id.btn_Volume) && !child.id.equals(R.id.btn_ship1)
                    && !child.id.equals(R.id.btn_ship) && !child.id.equals(R.id.btn_ship2) && !child.id.equals(
                        R.id.btn_ship3
                    ) &&
                    !child.id.equals(R.id.btn_ship4) && !child.id.equals(R.id.btn_ship5) && !child.id.equals(
                        R.id.btn_ship6
                    ) && !child.id.equals(R.id.btn_ship7)
                    && !child.id.equals(R.id.btn_ship8) && !child.id.equals(R.id.btn_ship9) && !child.id.equals(
                        R.id.btn_revolve_ship
                    ) && !child.id.equals(R.id.btn_remove_ship) && !child.id.equals(R.id.btn_gameStart)
                ) {
                    box_List.add(GameBoard(child))
                } else {
                    if (!child.id.equals(R.id.btn_Volume) && !child.id.equals(R.id.btn_revolve_ship) && !child.id.equals(
                            R.id.btn_remove_ship
                        ) && !child.id.equals(R.id.btn_gameStart)
                    ) {
                        ship_List.add(child)
                    }
                }
            } else if (child is ViewGroup) loopThrough(child)
        }
        for (i in box_List.indices) {
            box_List[i].button.id = i
            box_List[i].button.isClickable = false
        }
    }

    //Haritanın kullanılamayacak yapan fonksiyon
    fun closeMap() {
        for (i in 0..35) {
            if (!box_List[i].isShip) {
                box_List[i].doItClosedSea()
                box_List[i].button.isClickable = false
            }
        }
    }

    //Gemileri yerleştirmek için heritayı hazırlayan fonksiyon
    fun openMap() {
        for (i in 0..35) {
            if (!box_List[i].isShip) {
                box_List[i].doItSea()
                box_List[i].button.isClickable = true
            }
        }
    }

    // gemilerin görünür olup olmadıgını kontrol eden fonksiyon
    fun contolShip(): Boolean {
        for (i in ship_List) {
            if (i.isVisible == true) {
                return false
            }
        }
        return true
    }

    //Gemileri haritaya yerleşiren fonksiyon
    fun doItShip(v: Button, size: Int): Boolean {
        if (size == 3) {
            if (direction == 0) {
                var k5 = false
                var k4 = false
                var i = 0
                var k = 0
                if (v.id < 36) {
                    i = 4
                    k = 5
                } else {
                    i = 40
                    k = 41
                }
                while (i < 72) {
                    if (v.id == i) {
                        k4 = true
                        break
                    } else if (v.id == k) {
                        k5 = true
                        break
                    }
                    i += 6
                    k += 6

                }
                if (k4) {
                    if (!box_List[v.id - 1].isShip && !box_List[v.id + 1].isShip) {
                        box_List[v.id].doItShip()
                        box_List[v.id].id = Id
                        box_List[v.id - 1].doItShip()
                        box_List[v.id - 1].id = Id
                        box_List[v.id + 1].doItShip()
                        box_List[v.id + 1].id = Id
                        Id++
                        direction = 0
                        return true
                    } else {
                        if (v.id < 36) {
                            erorToast()
                            openCloseRevolvebutton()
                            direction = 0
                        }
                        return false
                    }
                    closeMap()
                } else if (k5) {
                    if (!box_List[v.id - 1].isShip && !box_List[v.id - 2].isShip) {
                        box_List[v.id].doItShip()
                        box_List[v.id].id = Id
                        box_List[v.id - 1].doItShip()
                        box_List[v.id - 1].id = Id
                        box_List[v.id - 2].doItShip()
                        box_List[v.id - 2].id = Id
                        Id++
                        direction = 0
                        return true
                    } else {
                        if (v.id < 36) {
                            erorToast()
                            openCloseRevolvebutton()
                            direction = 0
                        }
                        return false
                    }
                    closeMap()

                } else {
                    if (!box_List[v.id + 1].isShip && !box_List[v.id + 2].isShip) {
                        box_List[v.id].doItShip()
                        box_List[v.id].id = Id
                        box_List[v.id + 1].doItShip()
                        box_List[v.id + 1].id = Id
                        box_List[v.id + 2].doItShip()
                        box_List[v.id + 2].id = Id
                        Id++
                        direction = 0
                        return true
                    } else {
                        if (v.id < 36) {
                            erorToast()
                            openCloseRevolvebutton()
                            direction = 0
                        }
                        return false
                    }
                    closeMap()
                }

            } else {
                if ((v.id >= 24 && v.id <= 29) || (v.id >= 60 && v.id <= 65)) {
                    if (!box_List[v.id - 6].isShip && !box_List[v.id + 6].isShip) {
                        box_List[v.id].doItShip()
                        box_List[v.id].id = Id
                        box_List[v.id - 6].doItShip()
                        box_List[v.id - 6].id = Id
                        box_List[v.id + 6].doItShip()
                        box_List[v.id + 6].id = Id
                        Id++
                        direction = 0
                        return true
                    } else {
                        if (v.id < 36) {
                            erorToast()
                            openCloseRevolvebutton()
                            direction = 0
                        }
                        return false
                    }
                    closeMap()
                } else if ((v.id >= 30 && v.id <= 35) || (v.id >= 66 && v.id <= 71)) {
                    if (!box_List[v.id - 6].isShip && !box_List[v.id - 12].isShip) {
                        box_List[v.id].doItShip()
                        box_List[v.id].id = Id
                        box_List[v.id - 6].doItShip()
                        box_List[v.id - 6].id = Id
                        box_List[v.id - 12].doItShip()
                        box_List[v.id - 12].id = Id
                        Id++
                        direction = 0
                        return true
                    } else {
                        if (v.id < 36) {
                            erorToast()
                            openCloseRevolvebutton()
                            direction = 0
                        }
                        return false
                    }
                    closeMap()

                } else {
                    if (!box_List[v.id + 6].isShip && !box_List[v.id + 12].isShip) {
                        box_List[v.id].doItShip()
                        box_List[v.id].id = Id
                        box_List[v.id + 6].doItShip()
                        box_List[v.id + 6].id = Id
                        box_List[v.id + 12].doItShip()
                        box_List[v.id + 12].id = Id
                        Id++
                        direction = 0
                        return true
                    } else {
                        if (v.id < 36) {
                            erorToast()
                            openCloseRevolvebutton()
                            direction = 0
                        }
                        return false
                    }
                    closeMap()
                }

            }
            direction = 0
        } else if (size == 2) {
            if (direction == 0) {
                var k5 = false
                var k = 0
                if (v.id < 36) {
                    k = 5
                } else {
                    k = 41
                }

                while (k < 72) {
                    if (v.id == k) {
                        k5 = true
                        break
                    }
                    k += 6
                }
                if (k5) {
                    if (!box_List[v.id - 1].isShip) {
                        box_List[v.id].doItShip()
                        box_List[v.id].id = Id
                        box_List[v.id - 1].doItShip()
                        box_List[v.id - 1].id = Id
                        Id++
                        direction = 0
                        return true
                    } else {
                        if (v.id < 36) {
                            erorToast()
                            openCloseRevolvebutton()
                            direction = 0
                        }
                        return false
                    }
                    closeMap()
                } else {
                    if (!box_List[v.id + 1].isShip) {
                        box_List[v.id].doItShip()
                        box_List[v.id].id = Id
                        box_List[v.id + 1].doItShip()
                        box_List[v.id + 1].id = Id
                        Id++
                        direction = 0
                        return true
                    } else {
                        if (v.id < 36) {
                            erorToast()
                            openCloseRevolvebutton()
                            direction = 0
                        }
                        return false
                    }
                    closeMap()
                }
            } else {
                if ((v.id >= 30 && v.id <= 35) || (v.id >= 66 && v.id <= 71)) {
                    if (!box_List[v.id - 6].isShip) {
                        box_List[v.id].doItShip()
                        box_List[v.id].id = Id
                        box_List[v.id - 6].doItShip()
                        box_List[v.id - 6].id = Id
                        Id++
                        direction = 0
                        return true
                    } else {
                        if (v.id < 36) {
                            erorToast()
                            openCloseRevolvebutton()
                            direction = 0
                        }
                        return false
                    }
                    closeMap()
                } else {
                    if (!box_List[v.id + 6].isShip) {
                        box_List[v.id].doItShip()
                        box_List[v.id].id = Id
                        box_List[v.id + 6].doItShip()
                        box_List[v.id + 6].id = Id
                        Id++
                        direction = 0
                        return true
                    } else {
                        if (v.id < 36) {
                            erorToast()
                            openCloseRevolvebutton()
                            direction = 0
                        }
                        return false
                    }
                    closeMap()
                }

            }

        } else {
            for (i in box_List) {
                if (i.button == v) {
                    if (!i.isShip) {
                        i.doItShip()
                        i.id = Id
                        Id++
                        direction = 0
                        return true
                    } else {
                        return false
                    }


                }
            }
            direction = 0

        }
        return false
    }

    //haritaya koyulan gemiyi seçim yerinden silen fonksiyon
    fun cleanShip(v: Button) {
        v.setBackgroundResource(R.drawable.ship3)
        v.isInvisible = true
        state = 0
        closeMap()
    }

    //Haritayı temizler görsellik
    fun cleanMap(id: Int) {
        var temp = 0
        for (i in box_List) {
            if (i.id == id) {
                i.doItClosedSea()
                temp++
            }
        }
        printBackShip(temp)
    }

    //Gemileri haritadan silindikten sonra seçim yerine koyan fonksiyon
    fun printBackShip(temp: Int) {
        when (temp) {
            3 -> {
                if (btn_ship.isInvisible) {
                    btn_ship.isInvisible = false
                } else {
                    btn_ship1.isInvisible = false
                }
            }
            2 -> {
                if (btn_ship2.isInvisible) {
                    btn_ship2.isInvisible = false
                } else if (btn_ship3.isInvisible) {
                    btn_ship3.isInvisible = false
                } else {
                    btn_ship4.isInvisible = false
                }
            }
            1 -> {
                if (btn_ship5.isInvisible) {
                    btn_ship5.isInvisible = false
                } else if (btn_ship6.isInvisible) {
                    btn_ship6.isInvisible = false
                } else if (btn_ship7.isInvisible) {
                    btn_ship7.isInvisible = false
                } else if (btn_ship8.isInvisible) {
                    btn_ship8.isInvisible = false
                } else {
                    btn_ship9.isInvisible = false
                }
            }
        }
    }

    //Yön döndürme butonunu açıp kapatan fonksiyon
    fun openCloseRemovebutton() {
        btn_remove_ship.isInvisible = btn_remove_ship.isInvisible == false
    }

    //Yön döndürme butonunu açıp kapatan fonksiyon
    fun openCloseRevolvebutton() {
        btn_revolve_ship.isInvisible = btn_revolve_ship.isInvisible == false
    }

    //Yanlış konum için hata mesajı
    fun erorToast() {
        SimpleToast.error(this, "Konumlandırılmak için uygun degil");
    }

    //Düşman haritasını hazırlayan fonksiyon
    fun setEnemyBoard() {
        for (i in 1..10) {

            if (i <= 2) {
                setEnemyShip(3)
            } else if (i <= 5) {
                setEnemyShip(2)
            } else {
                setEnemyShip(1)
            }
        }
        for (i in 36..71) {
            if (box_List[i].isShip) {
                box_List[i].doItClosedSea()
                box_List[i].isClosedSea = false
                box_List[i].isShip = true
            }
        }
    }

    //Düşman gemilerini yerleştiren fonksiyon
    fun setEnemyShip(size: Int) {
        var bool = false
        var random = 0
        while (!bool) {
            direction = Random.nextInt(0, 3)
            random = Random.nextInt(36, 71)
            if (doItShip(box_List[random].button, size)) {
                bool = true
            }
        }
    }

    //Kazananı kontrol eden fonksiyon
    fun whoWin(w: Int): Boolean {
        if (w == 1) {
            for (i in 0..35) {
                if (box_List[i].isShip) {
                    return false
                }
            }
            return true
        } else {
            for (i in 36..71) {
                if (box_List[i].isShip) {
                    return false
                }
            }
            return true
        }
    }

    //düşmanın saldırma fonksiyonu(Single Game)
    fun enemyAttack() {
        var target = Random.nextInt(0, 36)
        if (box_List[target].isShip) {
            box_List[target].doItDamagedShip()
            writeNotBox(2, target, true)
            if (whoWin(1)) {
                gameFinalDialog(0)
            } else {
                enemyAttack()
            }

        } else if (box_List[target].isClosedSea) {
            box_List[target].doItDamagedSea()
            for (i in 36..71) {
                box_List[i].button.isClickable = true
            }
            doItMyTurn()
            writeNotBox(2, target, false)
        } else if (box_List[target].isDamegedSea) {
            var a = 0
            for (i in 0..35) {
                if (box_List[i].isShip) {
                    a = 1
                }
            }
            if (a == 1)
                enemyAttack()
        } else if (box_List[target].isDamagedShip) {
            var a = 0
            for (i in 0..35) {
                if (box_List[i].isShip) {
                    a = 1
                }
            }
            if (a == 1)
                enemyAttack()
        }

    }

    //düşmana saldırılınca yapılackalar
    fun enemyBoardClick(v: View?) {
        if (MainActivity.music!!.main_theme_player.isPlaying && MainActivity.music!!.main_theme_player.isLooping)
            MainActivity.music?.playerBtnStart()
        if (v != null) {
            if (game_mod == "Single Game") {
                if (box_List[v.id].isShip) {
                    box_List[v.id].doItDamagedShip()
                    writeNotBox(1, v.id - 36, true)
                    if (whoWin(0)) {
                        gameFinalDialog(1)
                    }
                } else if (box_List[v.id].isDamagedShip || box_List[v.id].isDamegedSea) {
                    SimpleToast.info(this, "Yeni Bir Yer Deneyin")
                } else {
                    box_List[v.id].doItDamagedSea()
                    doItEnemyTurn()
                    closeTouchEnemyMap()
                    tv_notificationTable.text =
                        tv_notificationTable.text.toString() + "\nHamle bekleniyor\n "
                    var timer = object : CountDownTimer(2000, 1000) {
                        override fun onFinish() {
                            var a = 0
                            for (i in 0..35) {
                                if (box_List[i].isShip) {
                                    a = 1
                                }
                            }
                            if (a == 1)
                                enemyAttack()
                        }

                        override fun onTick(millisUntilFinished: Long) {
                            writeNotBox()
                        }
                    }.start()

                }
            } else {
                if (box_List[v.id].isShip) {
                    box_List[v.id].doItDamagedShip()
                    doItMyTurn()
                    if (whoWin(2)) {
                        tv_notificationTable.text =
                            tv_notificationTable.text.toString() + "\nKazandınız\n "
                        sendReceive!!.write(v.id.toString().toByteArray())
                        closeTouchEnemyMap()
                        object : CountDownTimer(2000, 1000) {
                            override fun onFinish() {
                                gameFinalDialog(1)
                            }

                            override fun onTick(millisUntilFinished: Long) {
                                writeNotBox()
                            }

                        }.start()

                    } else {
                        writeNotBox(1, v.id - 36, true)
                        sendReceive!!.write(v.id.toString().toByteArray())

                    }
                } else if (box_List[v.id].isDamagedShip || box_List[v.id].isShip) {
                    SimpleToast.info(this, "Yeni Bir Yer Deneyin")
                } else {
                    box_List[v.id].doItDamagedSea()
                    writeNotBox(1, v.id - 36, false)
                    doItEnemyTurn()
                    sendReceive!!.write(v.id.toString().toByteArray())
                    closeTouchEnemyMap()
                    tv_notificationTable.text =
                        tv_notificationTable.text.toString() + "\nHamle bekleniyor\n"

                }
            }


        }
    }

    //oyunu başlatma fonksiyonu
    fun gameStartClick(v: View?) {
        if (MainActivity.music!!.main_theme_player.isPlaying && MainActivity.music!!.main_theme_player.isLooping)
            MainActivity.music?.playerBtnStart()
        btn_remove_ship.isVisible = false
        if (v != null) {
            if (game_mod == "Single Game") {
                btn_gameStart.isVisible = false
                layout_right.isVisible = true
                for (i in 36..71) {
                    box_List[i].button.isClickable = true
                }
                closeTouchMyMap()
                setEnemyBoard()
                state = 3
                tv_notificationTable.isVisible = true
                doItMyTurn()
            } else {
                if (shipListControl()) {
                    SimpleToast.error(this, "Önce Gemilerinizi Koymalısınız")
                } else {
                    closeTouchMyMap()
                    tv_notificationTable.isVisible = true
                    btn_gameStart.isVisible = false
                    var k = 0
                    var tmp: String = ""
                    for (i in 0..35) {
                        if (box_List[i].isShip) {
                            tmp += "$i,"
                        }
                    }
                    sendReceive!!.write(tmp.toByteArray())
                    if (MainActivity.clientClass != null) {
                        closeTouchEnemyMap()
                        doItEnemyTurn()
                    } else {
                        openTouchEnemyMap()
                        doItMyTurn()
                    }
                    if (MainActivity.clientClass != null) {
                        object : CountDownTimer(2000, 2000) {
                            override fun onFinish() {
                                sendReceive!!.write("Start Game".toByteArray())
                                layout_right.isVisible = true
                            }

                            override fun onTick(millisUntilFinished: Long) {
                                SimpleToast.info(this@GameActivity, "Karşı Taraf Bekleniyor")
                            }

                        }.start()
                    } else {
                        layout_right.isVisible = true
                    }
                }
            }

        }
    }

    //gemi yönünü degiştirmek için
    fun revolveShip(v: View?) {
        if (MainActivity.music!!.main_theme_player.isPlaying && MainActivity.music!!.main_theme_player.isLooping)
            MainActivity.music?.playerBtnStart()
        if (direction == 0) {
            direction = 1
        } else {
            direction = 0
        }
        changePositionShip()
    }

    //gemilere tıklanınca olucaklar
    fun selectShipOnClick(v: View?) {
        changePositionShip()
        if (MainActivity.music!!.main_theme_player.isPlaying && MainActivity.music!!.main_theme_player.isLooping)
            MainActivity.music?.playerBtnStart()
        openCloseRevolvebutton()
        if (v != null) {
            if (state == 0) {
                v.setBackgroundResource(R.drawable.ship3_select)
                state = 1
                openMap()
                selected_ship = v as (Button)
            } else if (state != 0) {
                for (i in ship_List) {
                    i.setBackgroundResource(R.drawable.ship3)
                }
                state = 0
                closeMap()
                selected_ship = null
            }
        }
    }

    //müzikleri açıp kapatmak
    fun volumeOnOff(view: View) {
        if (MainActivity.music?.main_theme_player?.isPlaying!!) {
            MainActivity.music?.playerMainStop()
            btn_Volume.setBackgroundResource(R.drawable.volume_off)
        } else {
            MainActivity.music?.playerMainStart()
            btn_Volume.setBackgroundResource(R.drawable.volume_on)
        }
    }

    //Gemi haritadan kaldırma butonu
    fun removeShip(v: View?) {
        if (MainActivity.music!!.main_theme_player.isPlaying && MainActivity.music!!.main_theme_player.isLooping)
            MainActivity.music?.playerBtnStart()
        var selected_id = 0
        for (i in box_List) {
            if (i.button == selected_box) {
                selected_id = i.id

            }
        }
        cleanMap(selected_id)
        closeMap()
        openCloseRemovebutton()


    }

    //Kendi haritamıza tıklanınca olucaklar
    fun myMapOnClick(v: View?) {
        changePositionShip()
        if (MainActivity.music!!.main_theme_player.isPlaying && MainActivity.music!!.main_theme_player.isLooping)
            MainActivity.music?.playerBtnStart()
        if (!btn_revolve_ship.isInvisible)
            openCloseRevolvebutton()
        if (v != null) {
            if (state == 1) {
                if (selected_ship?.id?.equals(R.id.btn_ship)!! || selected_ship?.id?.equals(R.id.btn_ship1)!!) {
                    if (!box_List[v.id].isShip) {
                        if (doItShip(v as Button, 3))
                            cleanShip(selected_ship!!)
                    } else {
                        erorToast()
                        openCloseRevolvebutton()
                    }
                } else if (selected_ship?.id?.equals(R.id.btn_ship2)!! || selected_ship?.id?.equals(
                        R.id.btn_ship3
                    )!! || selected_ship?.id?.equals(R.id.btn_ship4)!!
                ) {
                    if (!box_List[v.id].isShip) {
                        if (doItShip(v as Button, 2))
                            cleanShip(selected_ship!!)
                    } else {
                        erorToast()
                        openCloseRevolvebutton()
                    }

                } else if (selected_ship != null) {
                    if (!box_List[v.id].isShip) {
                        if (doItShip(v as (Button), 1))
                            cleanShip(selected_ship!!)
                    } else {
                        erorToast()
                        openCloseRevolvebutton()
                    }

                }

            } else if (state == 0) {
                for (i in box_List) {
                    if (i.button == v) {
                        if (i.isShip) {
                            openCloseRemovebutton()
                            selected_box = v as Button

                        }
                    }
                }
            }
        }
        if (state != 3) {
            if (contolShip()) {
                if (game_mod == "Single Game")
                    btn_gameStart.isVisible = true
                else if (MainActivity.clientClass != null) {
                    btn_gameStart.isVisible = true
                }
                closePositionShip()
            }
        }
    }

    // oyun bitiş ekranı
    fun gameFinalDialog(won: Int) {
        var alert = AlertDialog.Builder(this)
        var mView: View = layoutInflater.inflate(R.layout.game_final_dialog, null)
        var btnReGame = mView.btn_regame
        var btnReMain = mView.btn_remain
        var winBox = mView.tw_GameOver
        alert.setView(mView)
        var alertDialog: AlertDialog = alert.create()
        alertDialog.setCanceledOnTouchOutside(false)
        if (won == 1) {
            if (game_mod != "Single Game")
                sendReceive!!.write("won".toByteArray())
            winBox.text = "Kazandınız!!Tebrikler"
        } else {
            winBox.text = "Kaybettiniz!!"
        }
        btnReGame.setOnClickListener {
            cleanGame()
            if (game_mod == "Single Game") {
                alertDialog.dismiss()
                MainActivity.app_state_mode = "New Activity"
                recreate()
            } else {
                alertDialog.dismiss()
                MainActivity.app_state_mode = "New Activity"
                recreate()
            }


        }
        btnReMain.setOnClickListener {
            if (game_mod == "Single Game") {
                game_start_close_state = "direct"
                alertDialog.dismiss()
                onBackPressed()
            } else {
                object : CountDownTimer(2000, 2000) {
                    override fun onFinish() {
                        alertDialog.dismiss()
                        game_start_close_state = "Close Game"
                        onBackPressed()
                    }

                    override fun onTick(millisUntilFinished:  Long) {
                        sendReceive!!.write("Disconnect".toByteArray())
                    }

                }.start()
            }

        }
        alertDialog.show()
    }

    //bilgilendirme notu
    fun writeNotBox(turn: Int, id: Int, boolean: Boolean) {
        if (turn == 1) {
            if (boolean) {

                tv_notificationTable.text =
                    tv_notificationTable.text.toString() + "\nDüşmanın " + (id + 1) + ". bölgesini başarıyla bombaladınız."
            } else {
                tv_notificationTable.text =
                    tv_notificationTable.text.toString() + "\nDüşmanın " + (id + 1) + ". bölgesini ıskaladınız."
            }
        } else {
            if (boolean) {
                tv_notificationTable.text =
                    tv_notificationTable.text.toString() + "\nDüşman " + (id + 1) + ". bölgenizi bombaladı."
            } else {
                tv_notificationTable.text =
                    tv_notificationTable.text.toString() + "\nDüşman " + (id + 1) + ". bölgenizi ıskaladı."
            }

        }
        tv_notificationTable.post {
            val scrollAmount =
                tv_notificationTable.layout.getLineTop(tv_notificationTable.lineCount) - tv_notificationTable.height
            tv_notificationTable.scrollTo(0, scrollAmount)
        }
    }

    //bilgilendirme notu
    fun writeNotBox() {
        tv_notificationTable.text = tv_notificationTable.text.toString() + "."
        tv_notificationTable.post {
            val scrollAmount =
                tv_notificationTable.layout.getLineTop(tv_notificationTable.lineCount) - tv_notificationTable.height
            tv_notificationTable.scrollTo(0, scrollAmount)
        }
    }

    //kendi haritamızı kapatmak
    fun closeTouchMyMap() {
        for (i in 0..35) {
            box_List[i].button.isClickable = false
        }
    }

    //düşman haritasını kapatmak
    fun closeTouchEnemyMap() {
        for (i in 36..71) {
            box_List[i].button.isClickable = false
        }
    }

    //düşman haritasını açmak
    fun openTouchEnemyMap() {
        for (i in 36..71) {
            box_List[i].button.isClickable = true
        }
    }

    //bizim sırmız için görsellik
    fun doItMyTurn() {
        for (i in 36..71) {
            if (!box_List[i].isDamagedShip && !box_List[i].isDamegedSea)
                box_List[i].doItMyturn()
        }
    }

    //Düşman sırası için görsellki
    fun doItEnemyTurn() {
        for (i in 36..71) {
            if (!box_List[i].isDamagedShip && !box_List[i].isDamegedSea)
                box_List[i].doItEnemyTurn()
        }
    }

    //Soket haberleşmesi sonucu mesajın kontrol edilip yapılacak işlemeler
    fun setHandler() {
        var receive: String
        handler = Handler(Handler.Callback { msg ->
            when (msg.what) {
                STATE_MESSAGE_RECEIVED -> {
                    var readBuff: ByteArray = msg.obj as ByteArray
                    var tempMsg = String(readBuff, 0, msg.arg1)
                    receive = tempMsg
                    if (receive.length > 15) {
                        var tempArray: List<String> = receive.split(",")
                        for (i in 0..16) {
                            box_List[tempArray[i].toInt() + 36].isShip = true
                            box_List[tempArray[i].toInt() + 36].isClosedSea = false
                        }

                    } else if (receive == "won") {
                        gameFinalDialog(2)

                    } else if (receive == "Disconnect") {
                        game_start_close_state = "Close Game"
                        onBackPressed()
                    } else if (receive == "Start Game") {
                        btn_gameStart.isVisible = true
                    } else {
                        var id = receive.toInt() - 36
                        if (box_List[id].isShip) {
                            box_List[id].doItDamagedShip()
                            writeNotBox(2, id, true)
                        } else {
                            box_List[id].doItDamagedSea()
                            openTouchEnemyMap()
                            writeNotBox(2, id, false)
                            doItMyTurn()
                        }
                    }

                }
            }
            true
        })
    }

    //Yeni oyun için temizlik
    fun cleanGame() {
        box_List.clear()
        state = 0
        direction = 0
        selected_ship = null
        selected_box = null
        game_start_close_state = "New"

    }


    //gemi listesini kontrol ediyor
    fun shipListControl(): Boolean {
        for (i in ship_List) {
            if (i.isVisible) {
                return true
            }
        }
        return false
    }

    // gemi pozisyon görselini değiştiren fonksiyon
    fun changePositionShip() {
        if (direction == 0) {
            img_ShipState.setBackgroundResource(R.drawable.ship3)
        } else {
            img_ShipState.setBackgroundResource(R.drawable.ship3_turn)
        }
    }

    // gemi pozisyon göstergesini ortadan kaldıran fonksiyon
    fun closePositionShip() {
        tv_positonText.isVisible = false
        img_ShipState.isVisible = false
    }
}
