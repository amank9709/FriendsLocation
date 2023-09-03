package com.example.friendslocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMapFragment: SupportMapFragment
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var maps: GoogleMap? = null
    private lateinit var CurrLocImgBtn: ImageButton
    private lateinit var firebaseAuth: FirebaseAuth
    private var mFirebaseDatabaseInstances: FirebaseDatabase? = null
    private var context: Context? = null;
    private lateinit var locationHelper: LocationHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InitializeComponenets()
        this.context = this;
        locationHelper = LocationHelper(this);
        mMapFragment = supportFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        mMapFragment.getMapAsync(this)


    }

    private fun InitializeComponenets() {
        firebaseAuth = FirebaseAuth.getInstance()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        CurrLocImgBtn = findViewById(R.id.LocationFinderBtn)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,  IntentFilter("GPSLocationUpdates")
        );
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra("Status")
            val b = intent.getBundleExtra("Location")
            val location  = b?.getParcelable<Location>("Location")
            val latLng = location?.let { LatLng(it.latitude, location.longitude) }
            latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }?.let { maps?.moveCamera(it) }
            //Toast.makeText(this@MainActivity, "Location : ${b.toString()}",Toast.LENGTH_SHORT ).show()

            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
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
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )

        maps!!.setOnCameraIdleListener {

        }
    }


    @SuppressLint("MissingPermission")
    private fun getLiveLocation() {
//        maps?.isMyLocationEnabled = true
//        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
//            if (location != null) {
//                val latLng = LatLng(location.latitude, location.longitude)
//                maps?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
//            }
//
//        }
        Intent(applicationContext,LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }
    }



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


    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLiveLocation()
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

