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
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.friendslocation.HelperClasses.LocationHelper
import com.example.friendslocation.Services.LocationService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMapFragment: SupportMapFragment
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var maps: GoogleMap? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var locationHelper: LocationHelper
    private lateinit var markerOptions: MarkerOptions


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeComponents()
        setUpMap()
    }

    private fun initializeComponents() {
        locationHelper = LocationHelper(this)
        firebaseAuth = FirebaseAuth.getInstance()
        markerOptions = MarkerOptions()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver, IntentFilter("GPSLocationUpdates")
        )
    }

    @SuppressLint("ResourceType")
    private fun setUpMap() {

        // Find myLocationButton view
        mMapFragment = (supportFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment?)!!
        mMapFragment.getMapAsync(this)
        val myLocationButton = mMapFragment.view?.findViewById<View>(0x2)
        if (myLocationButton != null && myLocationButton.layoutParams is RelativeLayout.LayoutParams) {
            // location button is inside of RelativeLayout
            val params = myLocationButton.layoutParams as RelativeLayout.LayoutParams

            // Align it to - parent BOTTOM|LEFT
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)

            // Update margins, set to 10dp
            val margin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f,
                resources.displayMetrics
            ).toInt()
            params.setMargins(margin, margin, margin, margin)
            myLocationButton.layoutParams = params
        }
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val b = intent.getBundleExtra("Location")
            val location = b?.getParcelable<Location>("Location")
            val latLng = location?.let { LatLng(it.latitude, location.longitude) }
            latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 15f) }?.let { maps?.moveCamera(it) }

        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.maps = map

        if (locationHelper.checkLocationPermission())
            getLiveLocation()
        else
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION )

    }


    @SuppressLint("MissingPermission")
    private fun getLiveLocation() {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }
        maps?.isMyLocationEnabled = true
        showOthersLocation()
    }

    private fun showOthersLocation() {
        val databaseReference =
            FirebaseDatabase.getInstance("https://friendslocation-86b3f-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("LiveLocation")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //Toast.makeText(this@MainActivity,"Data: ${snapshot.child("CPKRbPa1qCOnxjRbZbNN4cxQra12").child("latitude")}", Toast.LENGTH_LONG).show()
                val latitude = snapshot.child("CPKRbPa1qCOnxjRbZbNN4cxQra12").child("latitude")
                    .getValue() as Double
                val longitude = snapshot.child("CPKRbPa1qCOnxjRbZbNN4cxQra12").child("longitude")
                    .getValue() as Double
                val latLng = LatLng(latitude, longitude)
                Toast.makeText(
                    this@MainActivity,
                    "lat: $latitude , long: $longitude",
                    Toast.LENGTH_LONG
                ).show()

                updateMarkerInMap(latLng)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Data: $error", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun updateMarkerInMap(latLng: LatLng) {

        maps?.addMarker(markerOptions.position(latLng))

    }


    override fun onPause() {
        super.onPause()
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            startService(this)
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

}

