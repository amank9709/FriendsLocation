package com.example.friendslocation.Model

import java.sql.Timestamp

data class LiveLocation (
    var latitude: Double,
    var longitude: Double,
    var timestamp: Timestamp
)