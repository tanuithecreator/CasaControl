package com.example.casacontrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.casacontrol.R
import com.example.casacontrol.adapters.DeviceAdapter
import com.example.casacontrol.adapters.RoomAdapter
import com.example.casacontrol.databinding.FragmentDevicesBinding
import com.example.casacontrol.models.Device
import com.example.casacontrol.models.Room
import com.google.firebase.firestore.FirebaseFirestore

class DevicesFragment : Fragment() {

    private var _binding: FragmentDevicesBinding? = null
    private val binding get() = _binding!!

    private val rooms = mutableListOf<Room>()
    private val devices = mutableListOf<Device>()
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDevicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up Room RecyclerView
        roomAdapter = RoomAdapter(rooms) { room ->
            // When a room is clicked, fetch devices for that room
            fetchDevicesForRoom(room)
        }
        binding.roomsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.roomsRecyclerView.adapter = roomAdapter

        // Set up Device RecyclerView (initially empty)
        deviceAdapter = DeviceAdapter(devices) { device ->
            toggleDevice(device)
        }
        binding.devicesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.devicesRecyclerView.adapter = deviceAdapter

        // Fetch rooms from Firestore
        fetchRooms()

        // Handle "Add Room" button click
        binding.addRoomButton.setOnClickListener {
            showAddRoomDialog()
        }

        // Handle "Add Device" button click
        binding.addDeviceButton.setOnClickListener {
            showAddDeviceDialog()
        }
    }

    private fun fetchRooms() {
        val db = FirebaseFirestore.getInstance()
        db.collection("rooms")
            .get()
            .addOnSuccessListener { result ->
                val roomList = mutableListOf<Room>()
                for (document in result) {
                    val room = document.toObject(Room::class.java)
                    roomList.add(room)
                }
                rooms.clear()
                rooms.addAll(roomList)
                roomAdapter.notifyDataSetChanged() // Refresh the room list
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching rooms: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchDevicesForRoom(room: Room) {
        val db = FirebaseFirestore.getInstance()
        db.collection("devices")
            .whereEqualTo("room", room.name) // Assuming "room" is a string in the Device model
            .get()
            .addOnSuccessListener { result ->
                val deviceList = mutableListOf<Device>()
                for (document in result) {
                    val device = document.toObject(Device::class.java)
                    deviceList.add(device)
                }
                devices.clear()
                devices.addAll(deviceList)
                deviceAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching devices: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun toggleDevice(device: Device) {
        device.isOn = !device.isOn
        val db = FirebaseFirestore.getInstance()
        db.collection("devices")
            .document(device.id) // Use the device's ID to update the correct document
            .update("isOn", device.isOn)
            .addOnSuccessListener {
                deviceAdapter.notifyDataSetChanged() // Refresh the device list
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error updating device: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddRoomDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_room, null)
        val roomNameEditText: EditText = dialogView.findViewById(R.id.roomNameEditText)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Room")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val roomName = roomNameEditText.text.toString()
                if (roomName.isNotEmpty()) {
                    addRoomToFirestore(roomName)
                } else {
                    Toast.makeText(requireContext(), "Room name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun addRoomToFirestore(roomName: String) {
        val db = FirebaseFirestore.getInstance()
        val room = Room(name = roomName)
        db.collection("rooms")
            .add(room)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Room added successfully", Toast.LENGTH_SHORT).show()
                fetchRooms() // Refresh room list
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error adding room: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddDeviceDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_device, null)
        val deviceNameEditText: EditText = dialogView.findViewById(R.id.deviceNameEditText)
        val deviceStatusSwitch: Switch = dialogView.findViewById(R.id.deviceStatusSwitch)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("Add Device")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val deviceName = deviceNameEditText.text.toString()
                val deviceStatus = deviceStatusSwitch.isChecked
                if (deviceName.isNotEmpty()) {
                    addDeviceToFirestore(deviceName, deviceStatus)
                } else {
                    Toast.makeText(requireContext(), "Device name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }

    private fun addDeviceToFirestore(deviceName: String, deviceStatus: Boolean) {
        val db = FirebaseFirestore.getInstance()
        val device = Device(name = deviceName, isOn = deviceStatus, room = "Living Room") // Adjust room as needed
        db.collection("devices")
            .add(device)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Device added successfully", Toast.LENGTH_SHORT).show()
                fetchDevicesForRoom(Room("Living Room")) // Fetch devices for room, adjust as necessary
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error adding device: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
