package com.example.casacontrol.models

// Data class to represent a device (e.g., light, fan, etc.)
data class Device(
    val id: String = "",
    val name: String = "",
    var isOn: Boolean = false,
    val room: String = "" // Room name or reference to a Room object
)
