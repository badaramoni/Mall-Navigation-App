package org.example.mymap.navigation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.mymap.MainActivity
import kotlin.math.roundToInt

class AndroidOrientationManager : OrientationManager, SensorEventListener {
    private val sensorManager = MainActivity.instance?.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    
    private val _heading = MutableStateFlow(0f)
    override val heading: StateFlow<Float> = _heading
    
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    
    override fun startListening() {
        sensorManager?.let { manager ->
            manager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            manager.registerListener(
                this,
                magnetometer,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }
    
    override fun stopListening() {
        sensorManager?.unregisterListener(this)
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
                updateOrientationAngles()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
                updateOrientationAngles()
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this implementation
    }
    
    private fun updateOrientationAngles() {
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )
        
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        
        // Convert radians to degrees and normalize to 0-360
        val degrees = Math.toDegrees(orientationAngles[0].toDouble()).roundToInt()
        val normalizedDegrees = ((degrees + 360) % 360).toFloat()
        
        _heading.value = normalizedDegrees
    }
} 