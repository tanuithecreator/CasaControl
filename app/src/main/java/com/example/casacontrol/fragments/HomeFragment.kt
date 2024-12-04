package com.example.casacontrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.casacontrol.R
import com.example.casacontrol.adapters.DeviceAdapter
import com.example.casacontrol.models.Device

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceAdapter
    private val deviceList = mutableListOf<Device>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.rv_devices)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Add sample devices to the list
        deviceList.add(Device("1", "Light", "Bulb", true))  // Pass true as Boolean, not a String
        deviceList.add(Device("2", "Fan", "Electric Fan",false)) // Pass false as Boolean, not a String
        deviceList.add(Device("3", "Printer", "Printer", true))  // Pass true as Boolean, not a String

        // Set up the adapter with a callback for toggling device state
        deviceAdapter = DeviceAdapter(deviceList) { device, isChecked ->
            // Handle the toggle action (e.g., updating device state)
            device.isOn = isChecked
            // You can update the device's state in your data model or API here
        }

        // Set the adapter for RecyclerView
        recyclerView.adapter = deviceAdapter

        return view
    }
}