package com.example.casacontrol.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.casacontrol.R
import com.example.casacontrol.models.Device

class DeviceAdapter(
    private val devices: List<Device>,
    private val onDeviceToggle: (Device, Boolean) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.deviceNameTextView.text = device.deviceName
        holder.deviceStatusSwitch.isChecked = device.isOn

        holder.deviceStatusSwitch.setOnCheckedChangeListener { _, isChecked ->
            onDeviceToggle(device, isChecked) // Update device status when toggled
        }
    }

    override fun getItemCount(): Int = devices.size

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceNameTextView: TextView = itemView.findViewById(R.id.deviceNameTextView)
        val deviceStatusSwitch: Switch = itemView.findViewById(R.id.deviceStatusSwitch)
    }
}