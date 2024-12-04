package com.example.casacontrol.models

data class Room(
    val name: String = "",
    val devices: Map<String, Map<String, Any>> = emptyMap()
)
