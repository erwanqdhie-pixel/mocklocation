package com.example.simplemocklocation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var etLatitude: TextInputEditText
    private lateinit var etLongitude: TextInputEditText
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etLatitude = findViewById(R.id.etLatitude)
        etLongitude = findViewById(R.id.etLongitude)
        tvStatus = findViewById(R.id.tvStatus)

        val btnStart: Button = findViewById(R.id.btnStart)
        val btnStop: Button = findViewById(R.id.btnStop)
        val btnDevSettings: Button = findViewById(R.id.btnDevSettings)

        requestPermissionsIfNeeded()

        btnStart.setOnClickListener {
            val lat = etLatitude.text.toString().toDoubleOrNull()
            val lng = etLongitude.text.toString().toDoubleOrNull()

            if (lat == null || lng == null || lat !in -90.0..90.0 || lng !in -180.0..180.0) {
                Toast.makeText(this, "Masukkan koordinat yang valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val serviceIntent = Intent(this, MockLocationService::class.java).apply {
                putExtra("LAT", lat)
                putExtra("LNG", lng)
            }
            ContextCompat.startForegroundService(this, serviceIntent)

            tvStatus.text = "Status: Aktif ($lat, $lng)"
            tvStatus.setTextColor(Color.parseColor("#2E7D32"))
        }

        btnStop.setOnClickListener {
            stopService(Intent(this, MockLocationService::class.java))
            tvStatus.text = "Status: Tidak Aktif"
            tvStatus.setTextColor(Color.parseColor("#D32F2F"))
        }

        btnDevSettings.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
            } catch (e: Exception) {
                Toast.makeText(this, "Aktifkan Developer Options dulu di Settings", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        val perms = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        val missing = perms.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 100)
        }
    }
}
