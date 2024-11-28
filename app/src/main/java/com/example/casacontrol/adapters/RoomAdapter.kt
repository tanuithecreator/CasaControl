package com.example.casacontrol.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.casacontrol.R
import com.example.casacontrol.models.Room

class RoomAdapter(
    private val rooms: List<Room>, // Room list passed from the fragment
    private val onRoomClick: (Room) -> Unit // Function to handle room selection
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room, parent, false) // Assuming you have an item layout for rooms
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]
        holder.roomNameTextView.text = room.name
        holder.itemView.setOnClickListener {
            onRoomClick(room) // Trigger the click listener when a room is selected
        }
    }

    override fun getItemCount(): Int = rooms.size

    // ViewHolder for individual room item
    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomNameTextView: TextView = itemView.findViewById(R.id.roomNameTextView) // Ensure your layout has this ID
    }
}
