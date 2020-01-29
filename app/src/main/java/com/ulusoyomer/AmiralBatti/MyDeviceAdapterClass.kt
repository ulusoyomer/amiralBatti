package com.ulusoyomer.AmiralBatti

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.adapter_layout.view.*

class MyDeviceAdapterClass(var context: Context,var device_list:ArrayList<MyDevice>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var device = device_list[position]
        var view = LayoutInflater.from(context).inflate(R.layout.adapter_layout,null)
        var d_name = view.tv_DeviceName
        var d_address = view.tv_DeviceAdress
        d_name.text = device.device_name
        d_address.text = device.device_address
        return view

    }

    override fun getItem(position: Int): Any {
        return device_list[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return device_list.size
    }
}