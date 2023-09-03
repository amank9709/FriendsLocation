package com.example.friendslocation

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationServices
import com.google.android.play.integrity.internal.l
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class LocationService : Service(){

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO )
    private lateinit var locationClient: LocationClient;

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationFetcherClient(
            this,
            LocationServices.getFusedLocationProviderClient(this)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)

    }

    private fun start(){
        val notification = NotificationCompat.Builder(this, "Location")
            .setContentTitle("Sharing Live Location...")
            .setContentText("Location : null")
            .setSmallIcon(R.drawable.location_seach_blue)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLiveLocationUpdates(5000L)
            .catch { e ->e.printStackTrace();  Toast.makeText(this@LocationService,"Error Occured in Notification: $e", Toast.LENGTH_LONG).show() }
            .onEach { location: Location ->
                sendLocationUpdatesToActivity(location);
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                val updatedNotification = notification.setContentText("Location : ($lat, $long)")

                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())

    }

    private fun sendLocationUpdatesToActivity(location: Location) {
        val intent = Intent("GPSLocationUpdates")
        val b = Bundle()
        b.putParcelable("Location", location)
        intent.putExtra("Location", b)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun stop(){
        stopForeground(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}