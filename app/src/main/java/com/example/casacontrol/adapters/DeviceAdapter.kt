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
    private val devices: List<Device>,  // List of devices
    private val onDeviceToggle: (Device) -> Unit // Callback function to handle device toggle state changes
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    // Inflates the item layout for each device
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    // Binds the data from each device to the respective UI elements in the item layout
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]

        // Set the device name and switch state
        holder.deviceNameTextView.text = device.name
        holder.deviceStatusSwitch.isChecked = device.isOn

        // Listen for toggle state changes (on/off)
        holder.deviceStatusSwitch.setOnCheckedChangeListener { _, isChecked ->
            device.isOn = isChecked
            onDeviceToggle(device)  // Pass the updated device to the callback
        }
    }

    // Returns the total number of devices
    override fun getItemCount(): Int = devices.size

    // ViewHolder class to hold references to the views in the item layout
    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deviceNameTextView: TextView = itemView.findViewById(R.id.deviceNameTextView)
        val deviceStatusSwitch: Switch = itemView.findViewById(R.id.deviceStatusSwitch)
    }
}