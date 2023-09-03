package com.example.friendslocation

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.content.Context
import androidx.core.content.ContextCompat


class LocationHelper(private val context: Context): ActivityCompat() {


    private val RequestCode = 1


    fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

        return false
    }

    fun requestLocationPermission(): Boolean {

        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            RequestCode
        )
        return false
    }

    fun Context.hasLocationPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                )
    }




}