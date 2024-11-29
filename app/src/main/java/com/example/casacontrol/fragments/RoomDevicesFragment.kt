package com.example.casacontrol.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.casacontrol.adapters.DeviceAdapter
import com.example.casacontrol.databinding.FragmentRoomDevicesBinding
import com.example.casacontrol.models.Device
import com.google.firebase.firestore.FirebaseFirestore

class RoomDevicesFragment : Fragment() {

    private var _binding: FragmentRoomDevicesBinding? = null
    private val binding get() = _binding!!

    private val devices = mutableListOf<Device>()
    private lateinit var deviceAdapter: DeviceAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private var roomName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoomDevicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get arguments passed from NavController
        roomName = arguments?.getString("room_name")
        Log.d("RoomDevicesFragment", "Room Name: $roomName")

        // Setup RecyclerView
        deviceAdapter = DeviceAdapter(devices) { device, newState ->
            toggleDeviceState(device, newState)
        }
        binding.devicesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.devicesRecyclerView.adapter = deviceAdapter

        // Fetch devices from Firestore
        fetchDevices()
    }

    private fun fetchDevices() {
        roomName?.let { room ->
            firestore.collection("rooms")
                .document(room)
                .collection("devices")
                .get()
                .addOnSuccessListener { result ->
                    devices.clear()
                    for (document in result) {
                        val name = document.getString("name") ?: "Unknown"
                        val state = document.getBoolean("state") ?: false
                        devices.add(Device(name = name, state = state))
                    }
                    deviceAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.e("RoomDevicesFragment", "Error fetching devices: ${e.message}")
                }
        }
    }

    private fun toggleDeviceState(device: Device, newState: Boolean) {
        roomName?.let { room ->
            firestore.collection("rooms")
                .document(room)
                .collection("devices")
                .document(device.name)
                .update("state", newState)
                .addOnSuccessListener {
                    device.state = newState
                    deviceAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Log.e("RoomDevicesFragment", "Error toggling device: ${e.message}")
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
