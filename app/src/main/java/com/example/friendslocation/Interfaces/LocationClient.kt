package com.example.friendslocation.Interfaces

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {

    fun getLiveLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String): Exception()


}