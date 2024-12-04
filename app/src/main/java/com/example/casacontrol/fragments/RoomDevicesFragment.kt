package com.example.casacontrol.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.casacontrol.adapters.DeviceAdapter
import com.example.casacontrol.databinding.DialogAddDeviceBinding
import com.example.casacontrol.databinding.FragmentRoomDevicesBinding
import com.example.casacontrol.models.Device
import com.example.casacontrol.models.Room
import com.google.firebase.firestore.FirebaseFirestore

class RoomDevicesFragment : Fragment() {

    private var _binding: FragmentRoomDevicesBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private var roomName: String? = null  // To store the room name passed from DevicesFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRoomDevicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the room name from arguments passed from DevicesFragment
        roomName = arguments?.getString("room_name")

        // Set up the Add Device button click listener
        binding.addDeviceButton.setOnClickListener {
            showAddDeviceDialog()
        }

        // Here, you would fetch devices for this specific room from Firestore, based on roomName
        fetchDevicesForRoom()
    }

    // Method to fetch devices related to this room
    private fun fetchDevicesForRoom() {
        roomName?.let { room ->
            firestore.collection("rooms")
                .document(room)
                .collection("devices")
                .get()
                .addOnSuccessListener { result ->
                    // Handle the result and update the RecyclerView with the devices for this room
                    // Example: Update RecyclerView adapter
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error fetching devices: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Method to show the dialog for adding a device
    private fun showAddDeviceDialog() {
        val dialogBinding = DialogAddDeviceBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Device")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val deviceName = dialogBinding.deviceNameEditText.text.toString().trim()
                if (deviceName.isNotEmpty()) {
                    addDeviceToFirestore(deviceName)
                } else {
                    Toast.makeText(requireContext(), "Device name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    // Method to add a device to Firestore under the selected room
    private fun addDeviceToFirestore(deviceName: String) {
        roomName?.let { room ->
            val deviceData = hashMapOf("name" to deviceName)

            firestore.collection("rooms")
                .document(room)
                .collection("devices")
                .document(deviceName)
                .set(deviceData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Device added successfully", Toast.LENGTH_SHORT).show()
                    fetchDevicesForRoom()  // Refresh device list
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error adding device: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}