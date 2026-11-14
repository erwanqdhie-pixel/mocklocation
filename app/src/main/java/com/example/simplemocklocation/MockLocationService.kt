package com.example.simplemocklocation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.location.provider.ProviderProperties
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MockLocationService : Service() {

    private lateinit var locationManager: LocationManager
    private val handler = Handler(Looper.getMainLooper())
    private var latitude: Double = -6.9175
    private var longitude: Double = 107.6191
    private var isRunning = false

    private val providers = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER
    )

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (!isRunning) return
            pushMockLocation()
            handler.postDelayed(this, 1000L)
        }
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        latitude = intent?.getDoubleExtra("LAT", -6.9175) ?: -6.9175
        longitude = intent?.getDoubleExtra("LNG", 107.6191) ?: 107.6191

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Mock Location Aktif")
            .setContentText("Koordinat: $latitude, $longitude")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .build()

        startForeground(101, notification)

        if (setupTestProviders()) {
            isRunning = true
            handler.removeCallbacks(updateRunnable)
            handler.post(updateRunnable)
        } else {
            stopSelf()
        }

        return START_STICKY
    }

    private fun setupTestProviders(): Boolean {
        return try {
            for (provider in providers) {
                try {
                    locationManager.removeTestProvider(provider)
                } catch (_: Exception) {
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    locationManager.addTestProvider(
                        provider,
                        false, false, false, false, false, true, true,
                        ProviderProperties.POWER_USAGE_LOW,
                        ProviderProperties.ACCURACY_FINE
                    )
                } else {
                    @Suppress("DEPRECATION")
                    locationManager.addTestProvider(
                        provider,
                        false, false, false, false, false, true, true,
                        1, 1
                    )
                }
                locationManager.setTestProviderEnabled(provider, true)
            }
            true
        } catch (e: SecurityException) {
            Toast.makeText(
                this,
                "Pilih aplikasi ini dulu di Developer Options > Select mock location app!",
                Toast.LENGTH_LONG
            ).show()
            false
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun pushMockLocation() {
        for (provider in providers) {
            try {
                val mockLoc = Location(provider).apply {
                    latitude = this@MockLocationService.latitude
                    longitude = this@MockLocationService.longitude
                    altitude = 10.0
                    time = System.currentTimeMillis()
                    elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
                    accuracy = 3.0f
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        bearingAccuracyDegrees = 0.1f
                        verticalAccuracyMeters = 0.1f
                        speedAccuracyMetersPerSecond = 0.01f
                    }
                }
                locationManager.setTestProviderLocation(provider, mockLoc)
            } catch (_: Exception) {
            }
        }
    }

    override fun onDestroy() {
        isRunning = false
        handler.removeCallbacks(updateRunnable)
        for (provider in providers) {
            try {
                locationManager.setTestProviderEnabled(provider, false)
                locationManager.removeTestProvider(provider)
            } catch (_: Exception) {
            }
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Mock Location Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "mock_location_channel"
    }
}
