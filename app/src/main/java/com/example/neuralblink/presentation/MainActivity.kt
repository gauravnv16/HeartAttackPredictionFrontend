/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.neuralblink.presentation
import com.example.neuralblink.R

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.marginLeft
import org.w3c.dom.Text

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var heartRateSensor: Sensor? = null
    private lateinit var startButton: Button

    private var isMonitoring = false

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        startButton = findViewById(R.id.startButton)
        startButton.setOnClickListener {
            if (!isMonitoring) {
                startHeartRateMonitoring()
            } else {
                stopHeartRateMonitoring()
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BODY_SENSORS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.BODY_SENSORS)
        }

        // Enables Always-on
//        setAmbientEnabled()
    }

    private fun startHeartRateMonitoring() {
        if (heartRateSensor != null) {
            isMonitoring = true
            sensorManager.registerListener(
                this,
                heartRateSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            var params = startButton.layoutParams as ViewGroup.MarginLayoutParams

            startButton.text = "Stop Monitoring"
            params.setMargins(60,0,0,25)

            startButton.layoutParams = params

        } else {
            Toast.makeText(this, "Heart rate sensor not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopHeartRateMonitoring() {
        isMonitoring = false
        sensorManager.unregisterListener(this)
        startButton.text = "Start Monitoring"

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for heart rate sensor
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE) {
            val heartRate = event.values[0]
            // Do something with the heart rate value (e.g., display it)
           val result = findViewById<TextView>(R.id.heartRateTextView)

            result.text = "Heart rate: $heartRate";

         Toast.makeText(this, "Your are safe from heart attack", Toast.LENGTH_SHORT).show()
        }
    }
}