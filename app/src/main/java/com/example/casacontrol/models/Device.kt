package com.example.casacontrol.models

data class Device(
    var id: String,
    val deviceName: String,
    val deviceType: String,
    var isOn: Boolean
)