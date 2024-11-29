package com.example.casacontrol.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.casacontrol.databinding.ItemDeviceBinding
import com.example.casacontrol.models.Device

class DeviceAdapter(
    private val devices: List<Device>,
    private val onToggle: (Device, Boolean) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    inner class DeviceViewHolder(private val binding: ItemDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(device: Device) {
            binding.deviceNameTextView.text = device.name
            binding.deviceSwitch.isChecked = device.state
            binding.deviceSwitch.setOnCheckedChangeListener { _, isChecked ->
                onToggle(device, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val binding = ItemDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(devices[position])
    }

    override fun getItemCount() = devices.size
}
