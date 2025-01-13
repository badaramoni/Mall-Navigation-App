package org.example.mymap.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.CoreMotion.CMDeviceMotion
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSError
import platform.Foundation.NSOperationQueue

class IosOrientationManager : OrientationManager {
    private val motionManager = CMMotionManager()
    private val _heading = MutableStateFlow(0f)
    override val heading: StateFlow<Float> = _heading
    
    override fun startListening() {
        if (motionManager.deviceMotionAvailable) {
            motionManager.deviceMotionUpdateInterval = 0.1 // 10 updates per second
            motionManager.startDeviceMotionUpdates(NSOperationQueue.mainQueue) { motion: CMDeviceMotion?, error: NSError? ->
                motion?.attitude?.yaw?.let { yaw ->
                    // Convert radians to degrees and normalize to 0-360
                    val degrees = Math.toDegrees(yaw)
                    val normalizedDegrees = ((degrees + 360) % 360).toFloat()
                    _heading.value = normalizedDegrees
                }
            }
        }
    }
    
    override fun stopListening() {
        motionManager.stopDeviceMotionUpdates()
    }
} 