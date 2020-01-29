package com.ulusoyomer.AmiralBatti

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.github.pierry.simpletoast.SimpleToast
import com.karan.churi.PermissionManager.PermissionManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.device_llist_layout.view.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), ISTATE {
    companion object {
        var music: Music? = null //Çalan müzik için oluşturlan nesne
        var app_state_mode: String = "Background" //Uygulamanın arkaplana atılınca müzigin durması için oluşturdum değişken
        var serverClass: ServerClass? = null
        var clientClass: ClientClass? = null
    }
    lateinit var permissionManager: PermissionManager
    var btArray: ArrayList<BluetoothDevice> = ArrayList()
    var bluetoothAdapter: BluetoothAdapter? = null
    var myDevice_List = ArrayList<MyDevice>()
    var deviceAdapterClass: MyDeviceAdapterClass = MyDeviceAdapterClass(this, myDevice_List)
    private var backTime:Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissionManager = object : PermissionManager(){}
        permissionManager.checkAndRequestPermissions(this)
        NecessaryObject.noActionBar(this)
        NecessaryObject.noOffScreen(this)
        NecessaryHandler.contex = this
        music = Music(this)
        music?.playerMainStart()
        var animations =
            Animations(this, btn_Single, btn_Volume, btn_Multi, iv_Logo, btn_listenDevice)
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter!!.isEnabled) {
            serverClass = ServerClass(bluetoothAdapter, MY_UUID)
            serverClass?.start()
        }

    }

    override fun onBackPressed() {
        if(backTime+2000>System.currentTimeMillis()){
            super.onBackPressed()
        }
        else{
            SimpleToast.info(this,"Press Back Again to Exit")
        }
        backTime = System.currentTimeMillis()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionManager.checkResult(requestCode,permissions,grantResults)

    }

    override fun onStop() {
        super.onStop()
        if (app_state_mode == "Background")
            music?.playerMainStop()
    }

    override fun onStart() {
        super.onStart()
        if (app_state_mode == "Background")
            music?.playerMainStart()
        volumeControl()
        app_state_mode = "Background"
    }

    //Sesi açıp kapatan fonksiyon
    fun volumeOnOff(view: View) {
        if (music?.main_theme_player?.isPlaying!!) {
            music?.playerMainStop()
            btn_Volume.setBackgroundResource(R.drawable.volume_off)
        } else {
            music?.playerMainStart()
            btn_Volume.setBackgroundResource(R.drawable.volume_on)
        }
    }

    //Single game buttonuna basılınca yapılacaklar
    fun buttonSingleonClick(view: View) {
        if (music!!.main_theme_player.isPlaying && music!!.main_theme_player.isLooping)
            music?.playerBtnStart()
        app_state_mode = "New Activity"
        var intent = Intent(this, GameActivity::class.java)
        intent.putExtra("Game Mod", "Single Game")
        startActivity(intent)
        Animatoo.animateSlideLeft(this)
    }

    //Sesi kontrol eden fonk
    fun volumeControl() {
        if (!(music?.main_theme_player?.isPlaying!!)) {
            btn_Volume.setBackgroundResource(R.drawable.volume_off)
            music?.playerMainStop()
        } else {
            btn_Volume.setBackgroundResource(R.drawable.volume_on)
            music?.playerMainStart()
        }
    }

    //Multiplayer butonun basılınca yapılacaklar
    fun buttonMultionClick(view: View) {
        if (music!!.main_theme_player.isPlaying && music!!.main_theme_player.isLooping)
            music?.playerBtnStart()
        if (!bluetoothAdapter!!.isEnabled) {
            var enableadapter = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableadapter, 1)
        } else {
            var alert = AlertDialog.Builder(this)
            var mView: View = layoutInflater.inflate(R.layout.device_llist_layout, null)
            var listView_DeviceList = mView.lv_deviceList
            alert.setView(mView)
            var alertDialog: AlertDialog = alert.create()
            alertDialog.setCanceledOnTouchOutside(true)
            if (bluetoothAdapter!!.isDiscovering) {
                bluetoothAdapter?.cancelDiscovery()
            }
            bluetoothAdapter?.startDiscovery()
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            registerReceiver(receiver, filter)
            if (!myDevice_List.isNullOrEmpty()) {
                deviceAdapterClass.notifyDataSetChanged()
                listView_DeviceList.adapter = deviceAdapterClass
            }
            listView_DeviceList.setOnItemClickListener { parent, view, position, id ->
                bluetoothAdapter?.cancelDiscovery()
                alertDialog.dismiss()
                clientClass = btArray[position]?.let { ClientClass(it, MY_UUID) }
                if (clientClass != null) {
                    clientClass?.start()
                }
            }

            alertDialog.show()
        }

    }

    // Bluetooth butonu basılınca yapılacaklar
    fun listenOnClick(view: View) {
        if (music!!.main_theme_player.isPlaying && music!!.main_theme_player.isLooping)
            music?.playerBtnStart()
        if (!bluetoothAdapter!!.isEnabled) {
            var enableadapter = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableadapter, 1)
        } else {
            startActivity(discoverableIntent)
        }

    }

    //Bluetooth açma istegi için gerekli fonk
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                bluetoothAdapter?.enable()
                startActivity(discoverableIntent)
                if (serverClass == null) {
                    serverClass = ServerClass(bluetoothAdapter, MY_UUID)
                    serverClass?.start()
                }
            }
        }
    }

    //bluetooth cihazlarını bulmak ve listemeze ekleme yapan yer
    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action.toString()) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    var tmp = 0
                    var deviceName = device.name
                    var macAdress = device.address
                    if(deviceName == null){
                        deviceName = "unkown"
                    }
                    for (i in myDevice_List) {
                        if (i.device_address == device.address) {
                            tmp = 1
                        }
                    }
                    if (tmp == 0) {
                        btArray.add(device)
                        myDevice_List.add(MyDevice(deviceName, macAdress))
                        deviceAdapterClass.notifyDataSetChanged()
                    }
                }
            }
        }
    }
    //Bluetooth görünürlügü için gerekli izin
    val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
        putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
    }
}
