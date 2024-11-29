package com.example.casacontrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.casacontrol.R
import com.example.casacontrol.adapters.RoomAdapter
import com.example.casacontrol.databinding.DialogAddRoomBinding
import com.example.casacontrol.databinding.FragmentDevicesBinding
import com.example.casacontrol.models.Room
import com.google.firebase.firestore.FirebaseFirestore

class DevicesFragment : Fragment() {

    private var _binding: FragmentDevicesBinding? = null
    private val binding get() = _binding!!

    private val rooms = mutableListOf<Room>()
    private lateinit var roomAdapter: RoomAdapter

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDevicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomAdapter = RoomAdapter(rooms) { room ->
            openRoomDevicesFragment(room.name)
        }
        binding.roomsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.roomsRecyclerView.adapter = roomAdapter

        binding.addRoomButton.setOnClickListener {
            showAddRoomDialog()
        }

        fetchRooms()
    }

    private fun fetchRooms() {
        firestore.collection("rooms")
            .get()
            .addOnSuccessListener { result ->
                rooms.clear()
                for (document in result) {
                    val roomName = document.id
                    rooms.add(Room(name = roomName))
                }
                roomAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error fetching rooms: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddRoomDialog() {
        val dialogBinding = DialogAddRoomBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Room")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { _, _ ->
                val roomName = dialogBinding.roomNameEditText.text.toString().trim()
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
        val roomData = hashMapOf("devices" to hashMapOf<String, Any>())
        firestore.collection("rooms")
            .document(roomName)
            .set(roomData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Room added successfully", Toast.LENGTH_SHORT).show()
                fetchRooms()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error adding room: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openRoomDevicesFragment(roomName: String) {
        val fragment = RoomDevicesFragment()
        fragment.arguments = Bundle().apply {
            putString("room_name", roomName)
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
