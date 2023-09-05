package com.example.friendslocation

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.example.friendslocation.HelperClasses.LocationHelper
import com.example.friendslocation.Interfaces.LocationClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch


@SuppressLint("StaticFieldLeak")
private lateinit var locationHelper: LocationHelper;

class LocationFetcherClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    override fun getLiveLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            locationHelper = LocationHelper(context)
            if (!locationHelper.checkLocationPermission())
                throw LocationClient.LocationException("Missing Location Permission")

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled && !isNetworkEnabled){
                throw LocationClient.LocationException("GPS OR NETWORK is disabled")
            }

            val request = LocationRequest.create()
                .setInterval(interval).setFastestInterval(interval)


            val locationCallback = object : LocationCallback(){
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)

                    result.locations.lastOrNull()?.let { location: Location -> launch { send(location) } }

                }
            }

            client.requestLocationUpdates(request,locationCallback, Looper.getMainLooper())

            awaitClose { client.removeLocationUpdates(locationCallback) }
        }
    }


}