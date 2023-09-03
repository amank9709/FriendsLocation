package com.example.friendslocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMapFragment: SupportMapFragment
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var locationPermissionGranted = false

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private var maps: GoogleMap? = null
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private val DEFAULT_ZOOM = 15
    private lateinit var CurrLocImgBtn: ImageButton
    private lateinit var firebaseAuth: FirebaseAuth
    private var mFirebaseDatabaseInstances: FirebaseDatabase? = null
    private var context: Context? = null;
    private lateinit var locationHelper : LocationHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InitializeComponenets()
        this.context = this;
         locationHelper =  LocationHelper(this);


        mMapFragment = supportFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        mMapFragment.getMapAsync(this)


    }

    private fun InitializeComponenets() {
        firebaseAuth = FirebaseAuth.getInstance()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        CurrLocImgBtn = findViewById(R.id.LocationFinderBtn)
    }

    override fun onMapReady(map: GoogleMap) {
        this.maps = map

        //getLocationPermission()
        //getCurrentLocation()
        if (locationHelper.checkLocationPermission())
            getLiveLocation();
        else
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {/*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationHelper.checkLocationPermission()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {

                            maps?.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        lastKnownLocation!!.latitude, lastKnownLocation!!.longitude
                                    )
                                ).title("You are here")
                            )
                            maps?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude, lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                            updateLastKnowLocation(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude
                            )
                            //CurrLocImgBtn.setImageDrawable(getDrawable(R.drawable.location_search_black))

                        }
                    } else {
                        Toast.makeText(this, "Current location is null$task", Toast.LENGTH_LONG)
                            .show()

                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        maps?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                defaultLocation,
                                DEFAULT_ZOOM.toFloat()
                            )
                        )
                        maps?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLiveLocation(){
        maps?.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {location -> if (location != null){
            val latLng  = LatLng(location.latitude, location.longitude)
            maps?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }

        }
    }
//
//    @SuppressLint("ServiceCast")
//    private fun getLiveLocation() {
//        // Get the LocationManager object.
//        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        // Get the last known location of the user.
//        val lastKnownLocation = if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//
//            return
//        } else {
//            Toast.makeText(this, "LastKnownLocation Not Found", Toast.LENGTH_SHORT).show()
//
//        } as Location
//
//        Toast.makeText(this, "LastKnownLocation $lastKnownLocation", Toast.LENGTH_SHORT).show()
//
//
//        // If the last known location is not null, set the center of the map to it.
//        if (lastKnownLocation != null) {
//            maps?.moveCamera(
//                CameraUpdateFactory.newLatLngZoom(
//                    LatLng(
//                        lastKnownLocation.latitude,
//                        lastKnownLocation.longitude
//                    ), 15f
//                )
//            )
//        }
//
//        // Start location updates.
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener)
//    }


//    private val locationListener = object : LocationListener {
//        override fun onLocationChanged(location: Location) {
//            // The location has changed, so update the map.
//            maps.moveCamera(
//                CameraUpdateFactory.newLatLngZoom(
//                    LatLng(
//                        location.latitude,
//                        location.longitude
//                    ), 15f
//                )
//            )
//        }
//    }

    private fun updateLastKnowLocation(latitude: Double, longitude: Double) {
        Toast.makeText(this, "Updating last known location", Toast.LENGTH_SHORT).show()
        mFirebaseDatabaseInstances =
            FirebaseDatabase.getInstance("https://friendslocation-86b3f-default-rtdb.asia-southeast1.firebasedatabase.app")

        var databaseReference = mFirebaseDatabaseInstances!!.getReference("LiveLocation")
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val timestamp = ServerValue.TIMESTAMP

        val data = hashMapOf(
            "latitude" to latitude,
            "longitude" to longitude,
            "timestamp" to timestamp
        )

        databaseReference!!.child(userId).setValue(data).addOnSuccessListener {
            Toast.makeText(this, "Success $it", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
            Toast.makeText(this, "Failure $it", Toast.LENGTH_SHORT).show()

        }

    }

    private fun getLocationPermission() {/*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }


    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getCurrentLocationOnMap(view: View) {
        CurrLocImgBtn.setImageDrawable(getDrawable(R.drawable.location_seach_blue))
        getLiveLocation()

    }
}

