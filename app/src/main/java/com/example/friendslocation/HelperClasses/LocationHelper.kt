package com.example.friendslocation.HelperClasses

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue


class LocationHelper(private val context: Context): ActivityCompat() {


    private var mFirebaseDatabaseInstances: FirebaseDatabase? = null




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




     fun updateLastKnowLocation(location: Location) {
         val latitude = location.latitude
         val longitude = location.longitude
        //Toast.makeText(context, "Updating last known location", Toast.LENGTH_SHORT).show()
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
            //Toast.makeText(context, "Success $it", Toast.LENGTH_SHORT).show()
            Log.i("DataBase Reference", "DataUpdated Successfully")
        }.addOnFailureListener {
            //Toast.makeText(context, "Failure $it", Toast.LENGTH_SHORT).show()
            Log.i("DataBase Reference", "Update Failure $it")

        }

    }





}